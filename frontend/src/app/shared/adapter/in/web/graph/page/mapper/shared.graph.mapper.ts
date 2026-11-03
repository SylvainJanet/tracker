import type { EChartsCoreOption } from 'echarts/core';
import type {
  SharedGraphAxisView,
  SharedGraphView,
} from '../../model/view/shared.graph.model.view';
import { SharedGraphAxisPolicyMapper } from './shared.graph.axis-policy.mapper';

const MINIMUM_X_AXIS_LABEL_SPACING_IN_PIXELS = 80;

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
    rendering: {
      readonly compact?: boolean;
      readonly plottingWidth?: number;
      readonly intervalOverrides?: {
        readonly xAxis?: number;
        readonly yAxis?: number;
      };
    } = {},
  ): EChartsCoreOption {
    const visualMap = model.series
      .map((series, seriesIndex) => intensityVisualMapFor(series, seriesIndex))
      .filter((vm) => vm !== undefined);
    const compact = rendering.compact === true;
    const intervalOverrides = compact ? undefined : rendering.intervalOverrides;
    const graphPresentation = model.presentation;

    const xAxisMapping = axisMappingFor(
      graphPresentation?.xAxis,
      model.series.flatMap((series) => series.points.map((point) => point.x)),
      targetXAxisLabelCountFor(rendering.plottingWidth),
      intervalOverrides?.xAxis,
    );
    const yAxisMapping = axisMappingFor(
      graphPresentation?.yAxis,
      model.series.flatMap((series) => series.points.map((point) => point.y)),
      undefined,
      intervalOverrides?.yAxis,
    );

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
        ...xAxisMapping.rangeOptions,
        ...(compact ? { show: false } : {}),
        ...(graphPresentation === undefined
          ? {}
          : {
              name: graphPresentation.xAxis.label,
              nameLocation: 'middle',
              axisLabel: {
                formatter: graphPresentation.xAxis.formatValue,
                ...(xAxisMapping.labelValues === undefined
                  ? {}
                  : { customValues: [...xAxisMapping.labelValues] }),
              },
            }),
      },

      yAxis: {
        type: 'value',
        ...yAxisMapping.rangeOptions,
        ...(compact ? { show: false } : {}),
        ...(graphPresentation === undefined
          ? {}
          : {
              name: graphPresentation.yAxis.label,
              nameLocation: 'middle',
              axisLabel: {
                formatter: graphPresentation.yAxis.formatValue,
                ...(yAxisMapping.labelValues === undefined
                  ? {}
                  : { customValues: [...yAxisMapping.labelValues] }),
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

function targetXAxisLabelCountFor(plottingWidth: number | undefined): number | undefined {
  if (plottingWidth === undefined || !Number.isFinite(plottingWidth) || plottingWidth <= 0) {
    return undefined;
  }

  return Math.max(2, Math.floor(plottingWidth / MINIMUM_X_AXIS_LABEL_SPACING_IN_PIXELS) + 1);
}

function axisMappingFor(
  axis: SharedGraphAxisView | undefined,
  values: readonly number[],
  fallbackTargetLabelCount?: number,
  intervalOverride?: number,
) {
  const dataExtent = extentFor(values);
  const policyExtent = axis?.extent ?? dataExtent;
  const scale =
    axis?.policy === undefined || policyExtent === undefined
      ? undefined
      : SharedGraphAxisPolicyMapper.scaleFor(
          policyExtent,
          {
            ...axis.policy,
            ...(fallbackTargetLabelCount === undefined ? {} : { fallbackTargetLabelCount }),
          },
          intervalOverride,
        );
  const resolvedExtent = scale ?? axis?.extent;

  return {
    rangeOptions: {
      min: resolvedExtent?.minimum ?? ('dataMin' as const),
      max: resolvedExtent?.maximum ?? ('dataMax' as const),
      ...(scale === undefined
        ? {}
        : {
            interval: scale.interval,
            axisTick: {
              customValues: [...scale.labelValues],
            },
          }),
    },
    labelValues: scale?.labelValues,
  };
}

function extentFor(
  values: readonly number[],
): { readonly minimum: number; readonly maximum: number } | undefined {
  const firstValue = values[0];

  if (firstValue === undefined) {
    return undefined;
  }

  let minimum = firstValue;
  let maximum = firstValue;

  for (const value of values.slice(1)) {
    minimum = Math.min(minimum, value);
    maximum = Math.max(maximum, value);
  }

  return {
    minimum,
    maximum,
  };
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
