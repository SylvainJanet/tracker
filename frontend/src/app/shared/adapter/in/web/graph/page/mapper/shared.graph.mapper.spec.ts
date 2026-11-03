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
  it('maps consumer axis policies using explicit and series-derived extents', () => {
    const graph = new SharedGraphView(
      'Weight measurements.',
      [
        {
          label: 'Measured weight',
          color: '#2563eb',
          points: [{ x: 10, y: 81.9 }],
        },
        {
          label: '7-day rolling average',
          color: '#dc2626',
          points: [{ x: 20, y: 82 }],
        },
      ],
      {
        title: 'Weight trends',
        xAxis: {
          label: 'Elapsed day number',
          formatValue: String,
          extent: {
            minimum: 3,
            maximum: 30,
          },
          policy: {
            kind: 'discrete',
            intervals: [
              { maximumSpanExclusive: 14, interval: 1 },
              { maximumSpanExclusive: 84, interval: 7 },
              { maximumSpanExclusive: 168, interval: 14 },
            ],
            fallbackTargetLabelCount: 9,
          },
        },
        yAxis: {
          label: 'Weight (kg)',
          formatValue: String,
          policy: {
            kind: 'continuous',
            intervals: [
              { maximumSpanExclusive: 0.2, interval: 0.05 },
              { maximumSpanExclusive: 2, interval: 0.1 },
              { maximumSpanExclusive: 5, interval: 0.5 },
              { maximumSpanExclusive: 20, interval: 1 },
            ],
            fallbackTargetLabelCount: 8,
          },
        },
      },
    );

    expect(SharedGraphMapper.modelToOptions(graph)).toMatchObject({
      xAxis: {
        min: 3,
        max: 30,
        interval: 7,
        axisLabel: {
          customValues: [3, 10, 17, 24],
        },
        axisTick: {
          customValues: [3, 10, 17, 24],
        },
      },
      yAxis: {
        min: 81.85,
        max: 82.05,
        interval: 0.05,
        axisLabel: {
          customValues: [81.85, 81.9, 81.95, 82, 82.05],
        },
        axisTick: {
          customValues: [81.85, 81.9, 81.95, 82, 82.05],
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
  it('adjusts long-range x-axis labels to the available plotting width', () => {
    const graph = new SharedGraphView(
      'Long weight history.',
      [
        {
          label: 'Measured weight',
          color: '#2563eb',
          points: [
            { x: 3, y: 82.1 },
            { x: 403, y: 81.9 },
          ],
        },
      ],
      {
        title: 'Weight trends',
        xAxis: {
          label: 'Elapsed day number',
          formatValue: String,
          extent: {
            minimum: 3,
            maximum: 403,
          },
          policy: {
            kind: 'discrete',
            intervals: [
              { maximumSpanExclusive: 14, interval: 1 },
              { maximumSpanExclusive: 84, interval: 7 },
              { maximumSpanExclusive: 168, interval: 14 },
            ],
            fallbackTargetLabelCount: 9,
          },
        },
        yAxis: {
          label: 'Weight (kg)',
          formatValue: String,
        },
      },
    );

    const narrowOptions = SharedGraphMapper.modelToOptions(graph, {
      plottingWidth: 320,
    });
    const wideOptions = SharedGraphMapper.modelToOptions(graph, {
      plottingWidth: 960,
    });

    expect(narrowOptions).toMatchObject({
      xAxis: {
        interval: 100,
        axisLabel: {
          customValues: [3, 103, 203, 303, 403],
        },
      },
      series: [
        {
          data: [
            [3, 82.1],
            [403, 81.9],
          ],
        },
      ],
    });
    expect(wideOptions).toMatchObject({
      xAxis: {
        interval: 50,
        axisLabel: {
          customValues: [3, 53, 103, 153, 203, 253, 303, 353, 403],
        },
      },
      series: [
        {
          data: [
            [3, 82.1],
            [403, 81.9],
          ],
        },
      ],
    });
  });
  it('maps a user-selected axis interval override', () => {
    const graph = new SharedGraphView('Weight measurements.', [], {
      title: 'Weight trends',
      xAxis: {
        label: 'Elapsed day number',
        formatValue: String,
        extent: {
          minimum: 3,
          maximum: 30,
        },
        policy: {
          kind: 'discrete',
          intervals: [
            { maximumSpanExclusive: 14, interval: 1 },
            { maximumSpanExclusive: 84, interval: 7 },
            { maximumSpanExclusive: 168, interval: 14 },
          ],
          fallbackTargetLabelCount: 9,
        },
      },
      yAxis: {
        label: 'Weight (kg)',
        formatValue: String,
      },
    });

    expect(
      SharedGraphMapper.modelToOptions(graph, {
        intervalOverrides: {
          xAxis: 14,
        },
      }),
    ).toMatchObject({
      xAxis: {
        min: 3,
        max: 30,
        interval: 14,
        axisLabel: {
          customValues: [3, 17],
        },
        axisTick: {
          customValues: [3, 17],
        },
      },
    });
  });
});
