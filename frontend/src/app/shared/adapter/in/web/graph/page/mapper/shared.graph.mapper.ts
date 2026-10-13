import type { EChartsCoreOption } from 'echarts/core';
import type { SharedGraphView } from '../../model/view/shared.graph.model.view';

export class SharedGraphMapper {
  private constructor() {
    /* empty */
  }

  static accessibleDescriptionFor(model: SharedGraphView): string {
    const presentation = model.presentation;

    if (presentation === undefined) {
      return model.accessibleDescription;
    }

    return `${presentation.title}. ${model.accessibleDescription} X-axis: ${presentation.xAxis.label}. Y-axis: ${presentation.yAxis.label}.`;
  }

  static modelToOptions(
    model: SharedGraphView,
    rendering: { readonly compact?: boolean } = {},
  ): EChartsCoreOption {
    const visualMap = model.series
      .map((series, seriesIndex) => intensityVisualMapFor(series, seriesIndex))
      .filter((vm) => vm !== undefined);
    const compact = rendering.compact === true;
    const graphPresentation = model.presentation;

    return {
      ...(compact
        ? {
            animation: false,
            grid: {
              left: 0,
              right: 0,
              top: 4,
              bottom: 4,
            },
          }
        : {}),
      aria: {
        enabled: true,
        description: this.accessibleDescriptionFor(model),
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
        ...(compact ? { show: false } : {}),
        ...(graphPresentation === undefined
          ? {}
          : {
              name: graphPresentation.xAxis.label,
              nameLocation: 'middle',
              axisLabel: {
                formatter: graphPresentation.xAxis.formatValue,
              },
            }),
      },

      yAxis: {
        type: 'value',
        min: 'dataMin',
        max: 'dataMax',
        ...(compact ? { show: false } : {}),
        ...(graphPresentation === undefined
          ? {}
          : {
              name: graphPresentation.yAxis.label,
              nameLocation: 'middle',
              axisLabel: {
                formatter: graphPresentation.yAxis.formatValue,
              },
            }),
      },
      series: model.series.map((series) => {
        const intensityAppliedByVisualMap = hasIntensityVisualMap(series);

        return {
          name: series.label,
          type: 'line',
          data: series.points.map((point) => pointToData(point, intensityAppliedByVisualMap)),
          showSymbol: series.points.length === 1,
          itemStyle: {
            color: series.color,
          },
        };
      }),
      visualMap,
    };
  }
}

function intensityVisualMapFor(series: SharedGraphView['series'][number], seriesIndex: number) {
  if (!hasIntensityVisualMap(series)) {
    return undefined;
  }

  return {
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
  };
}

function hasIntensityVisualMap(series: SharedGraphView['series'][number]): boolean {
  return series.points.length >= 2 && series.points.some((point) => point.intensity !== undefined);
}

function pointToData(
  point: SharedGraphView['series'][number]['points'][number],
  intensityAppliedByVisualMap: boolean,
):
  | [number, number]
  | {
      readonly value: [number, number];
      readonly itemStyle: {
        readonly opacity: number;
      };
    } {
  if (point.intensity === undefined || intensityAppliedByVisualMap) {
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
  return 0.2 + 0.8 * intensity;
}
