import type {
  AnalysisWeightMeasurementView,
  AnalysisWeightRollingAverageView,
} from '../../model/view/analysis.weight.model.view';
import {
  type SharedGraphPresentationView,
  SharedGraphView,
  type SharedRollingAverageGraphTooltipView,
  type SharedGraphAxisExtentView,
  type SharedGraphAxisPolicyView,
} from '../../../../../../../../shared/api/shared.graph';

const MEASURED_WEIGHT_SERIES_LABEL = 'Measured weight';
const MEASURED_WEIGHT_SERIES_COLOR = '--color-action';

const DAY_NUMBER_AXIS_POLICY: SharedGraphAxisPolicyView = {
  kind: 'discrete',
  intervals: [
    { maximumSpanExclusive: 14, interval: 1 },
    { maximumSpanExclusive: 84, interval: 7 },
    { maximumSpanExclusive: 168, interval: 14 },
  ],
  fallbackTargetLabelCount: 9,
};

const WEIGHT_AXIS_POLICY: SharedGraphAxisPolicyView = {
  kind: 'continuous',
  intervals: [
    { maximumSpanExclusive: 0.2, interval: 0.05 },
    { maximumSpanExclusive: 2, interval: 0.1 },
    { maximumSpanExclusive: 5, interval: 0.5 },
    { maximumSpanExclusive: 20, interval: 1 },
  ],
  fallbackTargetLabelCount: 8,
};

export class AnalysisWeightGraphMapper {
  private constructor() {
    /* empty */
  }

  static presentation(dayNumberExtent?: SharedGraphAxisExtentView): SharedGraphPresentationView {
    return {
      title: 'Weight trends',
      xAxis: {
        label: 'Elapsed day number',
        formatValue: String,
        ...(dayNumberExtent === undefined ? {} : { extent: dayNumberExtent }),
        policy: DAY_NUMBER_AXIS_POLICY,
      },
      yAxis: {
        label: 'Weight (kg)',
        formatValue: formatWeightAxisValue,
        policy: WEIGHT_AXIS_POLICY,
      },
    };
  }

  static tooltipByDayNumberFor(
    measurements: readonly AnalysisWeightMeasurementView[],
    rollingAverages: readonly AnalysisWeightRollingAverageView[],
  ): Readonly<Record<number, SharedRollingAverageGraphTooltipView>> {
    const tooltipByDayNumber: Record<number, SharedRollingAverageGraphTooltipView> = {};

    for (const measurement of measurements) {
      tooltipByDayNumber[measurement.dayNumber] = {
        heading: measurement.date,
        primaryValue: {
          seriesLabel: MEASURED_WEIGHT_SERIES_LABEL,
          color: MEASURED_WEIGHT_SERIES_COLOR,
          formattedValue: formatWeight(measurement.weightInKg),
        },
        rollingAverages: [],
      };
    }

    for (const rollingAverage of rollingAverages) {
      for (const point of rollingAverage.points) {
        const currentTooltip: SharedRollingAverageGraphTooltipView = tooltipByDayNumber[
          point.dayNumber
        ] ?? {
          heading: point.date,
          primaryValue: {
            seriesLabel: MEASURED_WEIGHT_SERIES_LABEL,
            color: MEASURED_WEIGHT_SERIES_COLOR,
            formattedValue: 'No measurement',
          },
          rollingAverages: [],
        };

        tooltipByDayNumber[point.dayNumber] = {
          ...currentTooltip,
          rollingAverages: [
            ...currentTooltip.rollingAverages,
            {
              seriesLabel: rollingAverageSeriesLabel(rollingAverage.windowInDays),
              color: rollingAverageSeriesColor(rollingAverage.windowInDays),
              formattedValue: formatWeight(point.averageWeightInKgApproximation),
              coverage: {
                includedValueCount: point.includedMeasurementCount,
                windowInDays: rollingAverage.windowInDays,
              },
            },
          ],
        };
      }
    }

    return tooltipByDayNumber;
  }

  static graphFor(
    measurements: readonly AnalysisWeightMeasurementView[],
    rollingAverages: readonly AnalysisWeightRollingAverageView[],
    dayNumberExtent?: SharedGraphAxisExtentView,
  ): SharedGraphView {
    return new SharedGraphView(
      accessibleDescriptionFor(measurements, rollingAverages),
      [
        {
          label: MEASURED_WEIGHT_SERIES_LABEL,
          color: MEASURED_WEIGHT_SERIES_COLOR,
          points: measurements.map((measurement) => ({
            x: measurement.dayNumber,
            y: measurement.weightInKg,
          })),
        },
        ...rollingAverages.map((rollingAverage) => ({
          label: rollingAverageSeriesLabel(rollingAverage.windowInDays),
          color: rollingAverageSeriesColor(rollingAverage.windowInDays),
          points: rollingAverage.points.map((point) => ({
            x: point.dayNumber,
            y: point.averageWeightInKgApproximation,
            intensity: rollingAverageIntensity(
              point.includedMeasurementCount,
              rollingAverage.windowInDays,
            ),
          })),
        })),
      ],
      this.presentation(dayNumberExtent),
    );
  }
}

function accessibleDescriptionFor(
  measurements: readonly AnalysisWeightMeasurementView[],
  rollingAverages: readonly AnalysisWeightRollingAverageView[],
): string {
  const firstMeasurement = measurements[0];
  const lastMeasurement = measurements.at(-1);

  if (firstMeasurement === undefined || lastMeasurement === undefined) {
    return 'Line graph with no measured weights.';
  }

  const rollingAverageDescription =
    rollingAverages.length === 0
      ? ''
      : rollingAverages.length === 1
        ? ' and 1 rolling average'
        : ` and ${rollingAverages.length} rolling averages`;

  if (measurements.length === 1) {
    return `Line graph of 1 measured weight${rollingAverageDescription} on analysis day ${firstMeasurement.dayNumber}.`;
  }

  return `Line graph of ${measurements.length} measured weights${rollingAverageDescription} from analysis day ${firstMeasurement.dayNumber} to analysis day ${lastMeasurement.dayNumber}.`;
}

function rollingAverageIntensity(includedValueCount: number, windowInDays: number): number {
  return Math.pow(includedValueCount / windowInDays, windowInDays / 7);
}

function formatWeight(weightInKg: number): string {
  return `${weightInKg} kg`;
}

function formatWeightAxisValue(weightInKg: number): string {
  return Number(weightInKg.toFixed(2)).toString();
}

function rollingAverageSeriesLabel(windowInDays: number): string {
  return `${windowInDays}-day rolling average`;
}

function rollingAverageSeriesColor(windowInDays: number): string {
  return `--color-analysis-weight-rolling-${windowInDays}`;
}
