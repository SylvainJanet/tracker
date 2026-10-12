import type {
  AnalysisWeightMeasurementView,
  AnalysisWeightRollingAverageView,
} from '../../model/view/analysis.weight.model.view';
import {
  SharedGraphView,
  type SharedRollingAverageGraphTooltipView,
} from '../../../../../../../../shared/api/shared.graph';

const MEASURED_WEIGHT_SERIES_LABEL = 'Measured weight';
const MEASURED_WEIGHT_SERIES_COLOR = '--color-action';

export class AnalysisWeightGraphMapper {
  private constructor() {
    /* empty */
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
  ): SharedGraphView {
    return new SharedGraphView(accessibleDescriptionFor(measurements, rollingAverages), [
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
    ]);
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

function rollingAverageSeriesLabel(windowInDays: number): string {
  return `${windowInDays}-day rolling average`;
}

function rollingAverageSeriesColor(windowInDays: number): string {
  return `--color-analysis-weight-rolling-${windowInDays}`;
}
