import type { ECharts } from 'echarts/core';

import { isRecord } from '../../../../../../typescript/is-record';
import type { SharedGraphView } from '../../model/view/shared.graph.model.view';

export class SharedGraphInteractionMapper {
  private constructor() {
    /* empty */
  }

  static tooltipStateFrom(
    event: unknown,
    graphBounds: DOMRectReadOnly,
    viewportWidth?: number,
  ):
    | {
        readonly value: number;
        readonly pointerX: number;
        readonly viewportPointerX: number;
        readonly viewportPointerY: number;
        readonly positionBeforePointer: boolean;
      }
    | undefined {
    if (!isRecord(event)) {
      return undefined;
    }

    const pointerX = event['x'];
    const pointerY = event['y'];
    const value = xAxisValueFrom(event);

    if (
      typeof pointerX !== 'number' ||
      !Number.isFinite(pointerX) ||
      typeof pointerY !== 'number' ||
      !Number.isFinite(pointerY) ||
      value === undefined
    ) {
      return undefined;
    }

    const viewportPointerX = graphBounds.left + pointerX;
    const positioningPointerX = viewportWidth === undefined ? pointerX : viewportPointerX;
    const positioningWidth = viewportWidth ?? graphBounds.width;

    return {
      value,
      pointerX,
      viewportPointerX,
      viewportPointerY: graphBounds.top + pointerY,
      positionBeforePointer: positioningWidth > 0 && positioningPointerX > positioningWidth / 2,
    };
  }

  static graphRegionXAxisValueFrom(
    event: unknown,
    chart: ECharts,
    graph: SharedGraphView,
  ): number | undefined {
    const pointer = this.pointerCoordinatesFrom(event);

    if (pointer === undefined || !chart.containPixel({ gridIndex: 0 }, pointer)) {
      return undefined;
    }

    const converted = chart.convertFromPixel({ xAxisIndex: 0 }, pointer[0]);
    const x = Array.isArray(converted) ? converted[0] : converted;

    return typeof x === 'number' && Number.isFinite(x)
      ? nearestGraphXAxisValue(graph, x)
      : undefined;
  }

  static pointerCoordinatesFrom(event: unknown): [number, number] | undefined {
    if (!isRecord(event)) {
      return undefined;
    }

    const offsetX = event['offsetX'];
    const offsetY = event['offsetY'];

    return typeof offsetX === 'number' &&
      Number.isFinite(offsetX) &&
      typeof offsetY === 'number' &&
      Number.isFinite(offsetY)
      ? [offsetX, offsetY]
      : undefined;
  }

  static latestGraphXAxisValue(graph: SharedGraphView): number | undefined {
    let latest: number | undefined;

    for (const series of graph.series) {
      for (const point of series.points) {
        if (latest === undefined || point.x > latest) {
          latest = point.x;
        }
      }
    }

    return latest;
  }
}

function xAxisValueFrom(event: Record<string, unknown>): number | undefined {
  const coordinateSystems = event['dataByCoordSys'];

  if (!Array.isArray(coordinateSystems)) {
    return undefined;
  }

  for (const coordinateSystem of coordinateSystems) {
    if (!isRecord(coordinateSystem)) {
      continue;
    }

    const axes = coordinateSystem['dataByAxis'];

    if (!Array.isArray(axes)) {
      continue;
    }

    for (const axis of axes) {
      if (
        isRecord(axis) &&
        axis['axisDim'] === 'x' &&
        typeof axis['value'] === 'number' &&
        Number.isFinite(axis['value'])
      ) {
        return axis['value'];
      }
    }
  }

  return undefined;
}

function nearestGraphXAxisValue(graph: SharedGraphView, target: number): number | undefined {
  let nearest: number | undefined;

  for (const series of graph.series) {
    for (const point of series.points) {
      if (nearest === undefined || Math.abs(point.x - target) < Math.abs(nearest - target)) {
        nearest = point.x;
      }
    }
  }

  return nearest;
}
