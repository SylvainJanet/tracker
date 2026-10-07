import type { WeightAnalysisResultData } from '../../../../../../application/port/in/get-weight-analysis.use-case';
import type {
  AnalysisWeightMeasurementView,
  AnalysisWeightRollingAverageView,
  AnalysisWeightView,
} from '../../model/view/analysis.weight.model.view';
import {
  SharedGraphView,
  type SharedRollingAverageGraphTooltipView,
} from '../../../../../../../../shared/api/shared.graph';

const MEASURED_WEIGHT_SERIES_LABEL = 'Measured weight';
const MEASURED_WEIGHT_SERIES_COLOR = '--color-action';

export class AnalysisWeightMapper {
  private constructor() {
    /* empty */
  }

  static resultDataToView(resultData: WeightAnalysisResultData): AnalysisWeightView {
    const weightMeasurements = resultData.weightMeasurements.map((measurement) => ({
      date: measurement.date,
      dayNumber: measurement.dayNumber,
      weightInKg: measurement.weightInKg,
    }));

    const rollingAverages = resultData.rollingAverageSeries.map((rollingAverage) => ({
      windowInDays: rollingAverage.windowSize,
      points: rollingAverage.rollingAverages.map((point) => ({
        date: point.date,
        dayNumber: point.dayNumber,
        includedMeasurementCount: point.includedValues.length,
        averageWeightInKgApproximation: prettyApproximationFor(point),
        completeCalendarWindow: point.dayNumber >= rollingAverage.windowSize,
      })),
    }));

    return {
      timelineStartDate: resultData.timelineStartDate,
      range: {
        startDate: resultData.range.startDate,
        endDate: resultData.range.endDate,
      },
      weightMeasurements,
      rollingAverages,
      graphTooltipByDayNumber: weightGraphTooltipByDayNumberFor(
        weightMeasurements,
        rollingAverages,
      ),
      graph: weightGraphFor(weightMeasurements, rollingAverages),
    };
  }
}

function prettyApproximationFor(
  point: WeightAnalysisResultData['rollingAverageSeries'][number]['rollingAverages'][number],
): number {
  const prettyApproximation = point.rollingAverage.approximations.find(
    (approximation) => approximation.rounding === 'PRETTY',
  );

  if (prettyApproximation === undefined) {
    throw new Error('Weight analysis rolling average has no PRETTY approximation.');
  }

  return prettyApproximation.value;
}

function weightGraphTooltipByDayNumberFor(
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

function formatWeight(weightInKg: number): string {
  return `${weightInKg} kg`;
}

function rollingAverageSeriesLabel(windowInDays: number): string {
  return `${windowInDays}-day rolling average`;
}

function rollingAverageSeriesColor(windowInDays: number): string {
  return `--color-analysis-weight-rolling-${windowInDays}`;
}

function weightGraphFor(
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
        intensity: Math.pow(
          point.includedMeasurementCount / rollingAverage.windowInDays,
          rollingAverage.windowInDays / 7,
        ),
      })),
    })),
  ]);
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
