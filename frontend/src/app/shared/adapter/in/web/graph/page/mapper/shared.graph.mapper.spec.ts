import { LineChart } from 'echarts/charts';
import { GridComponent, TooltipComponent, VisualMapComponent } from 'echarts/components';
import * as echarts from 'echarts/core';
import { SVGRenderer } from 'echarts/renderers';
import { describe, expect, it, vi } from 'vitest';

import { SharedGraphView } from '../../model/view/shared.graph.model.view';
import { SharedGraphMapper } from './shared.graph.mapper';

echarts.use([LineChart, GridComponent, TooltipComponent, VisualMapComponent, SVGRenderer]);

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
      tooltip: {
        trigger: 'axis',
        showContent: false,
        axisPointer: {
          type: 'line',
          snap: true,
        },
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
            [1, 82.1],
            [4, 82],
            [7, 81.8],
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
      chart.dispatchAction({
        type: 'highlight',
        seriesIndex: 0,
        dataIndex: 0,
      });

      const renderedGraph = chart.renderToSVGString();
      const firstMarker = new DOMParser()
        .parseFromString(renderedGraph, 'image/svg+xml')
        .querySelector('[ecmeta_series_index="0"][ecmeta_data_index="0"]');

      expect(renderedGraph).toContain('stop-opacity="0.4"');
      expect(renderedGraph).toContain('stop-opacity="0.52"');
      expect(firstMarker?.getAttribute('stroke-opacity')).toBe('0.4');
    } finally {
      chart.dispose();
      getCanvasContext.mockRestore();
    }
  });

  it('keeps a single intensity point visible and applies its intensity once', () => {
    const graph = new SharedGraphView('One rolling-average point.', [
      {
        label: '7-day rolling average',
        color: '#dc2626',
        points: [{ x: 1, y: 82.1, intensity: 0.25 }],
      },
    ]);

    expect(SharedGraphMapper.modelToOptions(graph)).toMatchObject({
      tooltip: {
        trigger: 'axis',
        showContent: false,
        axisPointer: {
          type: 'line',
          snap: true,
        },
      },
      series: [
        {
          data: [
            {
              value: [1, 82.1],
              itemStyle: {
                opacity: 0.4,
              },
            },
          ],
          showSymbol: true,
        },
      ],
      visualMap: [],
    });
  });

  it('maps a compact graph without visible axes or animation while preserving hover', () => {
    const graph = new SharedGraphView('Compact weight trend.', [
      {
        label: 'Measured weight',
        color: '#2563eb',
        points: [
          { x: 1, y: 82.1 },
          { x: 4, y: 81.9 },
        ],
      },
    ]);

    expect(SharedGraphMapper.modelToOptions(graph, { compact: true })).toMatchObject({
      animation: false,
      grid: {
        left: 0,
        right: 0,
        top: 4,
        bottom: 4,
      },
      tooltip: {
        trigger: 'axis',
        showContent: false,
        axisPointer: {
          type: 'line',
          snap: true,
        },
      },
      xAxis: {
        show: false,
      },
      yAxis: {
        show: false,
      },
    });
  });
  it('maps generic axis labels and value formatters', () => {
    const formatDayNumber = (value: number): string => `${value}`;
    const formatWeight = (value: number): string => `${value} kg`;
    const graph = new SharedGraphView('Weight measurements.', [], {
      title: 'Weight trends',
      xAxis: {
        label: 'Elapsed day number',
        formatValue: formatDayNumber,
      },
      yAxis: {
        label: 'Weight (kg)',
        formatValue: formatWeight,
      },
    });

    expect(SharedGraphMapper.modelToOptions(graph)).toMatchObject({
      xAxis: {
        name: 'Elapsed day number',
        nameLocation: 'middle',
        axisLabel: {
          formatter: formatDayNumber,
        },
      },
      yAxis: {
        name: 'Weight (kg)',
        nameLocation: 'middle',
        axisLabel: {
          formatter: formatWeight,
        },
      },
    });
  });
  it('includes the title and axis names in the accessible description', () => {
    const graph = new SharedGraphView('Line graph of 1 measured weight on analysis day 1.', [], {
      title: 'Weight trends',
      xAxis: {
        label: 'Elapsed day number',
        formatValue: String,
      },
      yAxis: {
        label: 'Weight (kg)',
        formatValue: String,
      },
    });

    expect(SharedGraphMapper.modelToOptions(graph)).toMatchObject({
      aria: {
        enabled: true,
        description:
          'Weight trends. Line graph of 1 measured weight on analysis day 1. X-axis: Elapsed day number. Y-axis: Weight (kg).',
      },
    });
  });
});
