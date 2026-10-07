import { LineChart } from 'echarts/charts';
import { GridComponent, VisualMapComponent } from 'echarts/components';
import * as echarts from 'echarts/core';
import { SVGRenderer } from 'echarts/renderers';
import { describe, expect, it, vi } from 'vitest';

import { SharedGraphView } from '../../model/view/shared.graph.model.view';
import { SharedGraphMapper } from './shared.graph.mapper';

echarts.use([LineChart, GridComponent, VisualMapComponent, SVGRenderer]);

describe('SharedGraphMapper', () => {
  it('maps sparse series to lines without persistent point symbols', () => {
    const graph = new SharedGraphView('Measured weight and 7-day rolling average.', [
      {
        label: 'Measured weight',
        color: '#2563eb',
        points: [
          { x: 1, y: 82.1 },
          { x: 4, y: 81.9 },
        ],
      },
      {
        label: '7-day rolling average',
        color: '#dc2626',
        points: [
          { x: 1, y: 82.1, intensity: 0.25 },
          { x: 4, y: 82, intensity: 0.4 },
          { x: 7, y: 81.8, intensity: 1 },
        ],
      },
    ]);

    expect(SharedGraphMapper.modelToOptions(graph)).toEqual({
      aria: {
        enabled: true,
        description: 'Measured weight and 7-day rolling average.',
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
      series: [
        {
          name: 'Measured weight',
          type: 'line',
          data: [
            [1, 82.1],
            [4, 81.9],
          ],
          showSymbol: false,
          itemStyle: {
            color: '#2563eb',
          },
        },
        {
          name: '7-day rolling average',
          type: 'line',
          data: [
            {
              value: [1, 82.1],
              itemStyle: {
                opacity: 0.4,
              },
            },
            {
              value: [4, 82],
              itemStyle: {
                opacity: 0.52,
              },
            },
            {
              value: [7, 81.8],
              itemStyle: {
                opacity: 1,
              },
            },
          ],
          showSymbol: false,
          itemStyle: {
            color: '#dc2626',
          },
        },
      ],
      visualMap: [
        {
          type: 'piecewise',
          show: false,
          seriesIndex: 1,
          dimension: 0,
          pieces: [
            {
              gte: 1,
              lt: 4,
              colorAlpha: 0.4,
            },
            {
              gte: 4,
              lte: 7,
              colorAlpha: 0.52,
            },
          ],
        },
      ],
    });
  });

  it('keeps a symbol for a series containing only one point', () => {
    const graph = new SharedGraphView('One measured weight.', [
      {
        label: 'Measured weight',
        color: '#2563eb',
        points: [{ x: 1, y: 82.1 }],
      },
    ]);

    expect(SharedGraphMapper.modelToOptions(graph)).toMatchObject({
      series: [
        {
          showSymbol: true,
        },
      ],
    });
  });

  it('applies calculated intensities to the rendered line stroke', () => {
    const graph = new SharedGraphView('7-day rolling average.', [
      {
        label: '7-day rolling average',
        color: '#dc2626',
        points: [
          { x: 1, y: 82.1, intensity: 0.25 },
          { x: 4, y: 82, intensity: 0.4 },
          { x: 7, y: 81.8, intensity: 1 },
        ],
      },
    ]);
    const canvasContext = {
      font: '',
      measureText: (text: string) => ({ width: text.length * 7 }) as TextMetrics,
    } as CanvasRenderingContext2D;
    const getCanvasContext = vi
      .spyOn(HTMLCanvasElement.prototype, 'getContext')
      .mockReturnValue(canvasContext);
    const chart = echarts.init(null, null, {
      renderer: 'svg',
      ssr: true,
      width: 600,
      height: 400,
    });

    try {
      chart.setOption(SharedGraphMapper.modelToOptions(graph));

      const renderedGraph = chart.renderToSVGString();
      expect(renderedGraph).toContain('stop-opacity="0.4"');
      expect(renderedGraph).toContain('stop-opacity="0.52"');
    } finally {
      chart.dispose();
      getCanvasContext.mockRestore();
    }
  });
});
