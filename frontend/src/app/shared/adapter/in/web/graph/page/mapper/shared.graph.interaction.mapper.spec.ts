import type { ECharts } from 'echarts/core';
import { describe, expect, it, vi } from 'vitest';

import { SharedGraphView } from '../../model/view/shared.graph.model.view';
import { SharedGraphInteractionMapper } from './shared.graph.interaction.mapper';

const graph = new SharedGraphView('Weight graph.', [
  {
    label: 'Measured weight',
    color: '#2563eb',
    points: [
      { x: 1, y: 82.1 },
      { x: 4, y: 81.9 },
    ],
  },
  {
    label: 'Rolling average',
    color: '#dc2626',
    points: [{ x: 7, y: 81.8 }],
  },
]);

describe('SharedGraphInteractionMapper', () => {
  it('maps a pointer inside the plotting grid to the nearest graph value', () => {
    const chart = {
      containPixel: vi.fn(() => true),
      convertFromPixel: vi.fn(() => 3),
    } as unknown as ECharts;

    const value = SharedGraphInteractionMapper.graphRegionXAxisValueFrom(
      { offsetX: 120, offsetY: 80 },
      chart,
      graph,
    );

    expect(chart.containPixel).toHaveBeenCalledWith({ gridIndex: 0 }, [120, 80]);
    expect(chart.convertFromPixel).toHaveBeenCalledWith({ xAxisIndex: 0 }, 120);
    expect(value).toBe(4);
  });

  it('does not convert a pointer outside the plotting grid', () => {
    const chart = {
      containPixel: vi.fn(() => false),
      convertFromPixel: vi.fn(),
    } as unknown as ECharts;

    const value = SharedGraphInteractionMapper.graphRegionXAxisValueFrom(
      { offsetX: 120, offsetY: 280 },
      chart,
      graph,
    );

    expect(chart.containPixel).toHaveBeenCalledWith({ gridIndex: 0 }, [120, 280]);
    expect(chart.convertFromPixel).not.toHaveBeenCalled();
    expect(value).toBeUndefined();
  });

  it.each([
    null,
    [],
    {},
    { offsetX: Number.NaN, offsetY: 80 },
    { offsetX: 120, offsetY: Number.POSITIVE_INFINITY },
  ])('rejects invalid pointer coordinates', (event) => {
    expect(SharedGraphInteractionMapper.pointerCoordinatesFrom(event)).toBeUndefined();
  });

  it('finds the latest value across every graph series', () => {
    expect(SharedGraphInteractionMapper.latestGraphXAxisValue(graph)).toBe(7);
  });

  it('does not find a latest value in an empty graph', () => {
    expect(
      SharedGraphInteractionMapper.latestGraphXAxisValue(new SharedGraphView('Empty graph.', [])),
    ).toBeUndefined();
  });

  it('maps an ECharts tooltip event to viewport positioning state', () => {
    const graphBounds = {
      left: 500,
      top: 100,
      width: 200,
    } as DOMRectReadOnly;

    expect(
      SharedGraphInteractionMapper.tooltipStateFrom(
        {
          x: 120,
          y: 80,
          dataByCoordSys: [
            {
              dataByAxis: [
                {
                  axisDim: 'x',
                  value: 4,
                },
              ],
            },
          ],
        },
        graphBounds,
      ),
    ).toEqual({
      value: 4,
      pointerX: 120,
      viewportPointerX: 620,
      viewportPointerY: 180,
      positionBeforePointer: true,
    });
  });

  it('rejects an invalid ECharts tooltip event', () => {
    expect(
      SharedGraphInteractionMapper.tooltipStateFrom(
        {
          x: Number.NaN,
          y: 80,
          dataByCoordSys: [],
        },
        { left: 0, top: 0, width: 200 } as DOMRectReadOnly,
      ),
    ).toBeUndefined();
  });
});
