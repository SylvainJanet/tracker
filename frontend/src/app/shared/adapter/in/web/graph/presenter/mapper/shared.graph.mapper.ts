import type { EChartsCoreOption } from 'echarts/core';

import type { SharedGraphModel } from '../../model/shared.graph.model';

export class SharedGraphMapper {
  private constructor() {
    /* empty */
  }

  static modelToOptions(model: SharedGraphModel): EChartsCoreOption {
    const visualMap = model.series.flatMap((series, seriesIndex) =>
      intensityVisualMapFor(series, seriesIndex),
    );

    return {
      aria: {
        enabled: true,
        description: model.accessibleDescription,
      },
      xAxis: {
        type: 'value',
        min: 'dataMin',
        max: 'dataMax',
      },
      yAxis: {
        type: 'value',
        min: 'dataMin',
        max: 'dataMax',
      },
      series: model.series.map((series) => ({
        name: series.label,
        type: 'line',
        data: series.points.map(pointToData),
        showSymbol: series.points.length === 1,
        ...(hasIntensityVisualMap(series)
          ? {}
          : {
              lineStyle: {
                color: series.color,
              },
            }),
        itemStyle: {
          color: series.color,
        },
      })),
      ...(visualMap.length === 0 ? {} : { visualMap }),
    };
  }
}

function intensityVisualMapFor(series: SharedGraphModel['series'][number], seriesIndex: number) {
  if (!hasIntensityVisualMap(series)) {
    return [];
  }

  return [
    {
      type: 'piecewise' as const,
      show: false,
      seriesIndex,
      dimension: 0,
      pieces: series.points.slice(0, -1).map((point, pointIndex) => {
        const nextPoint = series.points[pointIndex + 1]!;
        const colorAlpha = opacityForIntensity(
          Math.min(point.intensity ?? 1, nextPoint.intensity ?? 1),
        );

        return pointIndex === series.points.length - 2
          ? { gte: point.x, lte: nextPoint.x, colorAlpha }
          : { gte: point.x, lt: nextPoint.x, colorAlpha };
      }),
    },
  ];
}

function hasIntensityVisualMap(series: SharedGraphModel['series'][number]): boolean {
  return series.points.length >= 2 && series.points.some((point) => point.intensity !== undefined);
}

function pointToData(point: SharedGraphModel['series'][number]['points'][number]):
  | [number, number]
  | {
      readonly value: [number, number];
      readonly itemStyle: {
        readonly opacity: number;
      };
    } {
  if (point.intensity === undefined) {
    return [point.x, point.y];
  }

  return {
    value: [point.x, point.y],
    itemStyle: {
      opacity: opacityForIntensity(point.intensity),
    },
  };
}

function opacityForIntensity(intensity: number): number {
  return 0.25 + 0.75 * intensity;
}
