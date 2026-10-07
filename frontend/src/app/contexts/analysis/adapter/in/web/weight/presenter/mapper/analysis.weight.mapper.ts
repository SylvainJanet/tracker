import type { WeightAnalysisResultData } from '../../../../../../application/port/in/get-weight-analysis.use-case';
import type {
  AnalysisWeightMeasurementView,
  AnalysisWeightRollingAverageView,
  AnalysisWeightView,
} from '../../model/view/analysis.weight.model.view';
import { SharedGraphView } from '../../../../../../../../shared/api/shared.graph';

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

function weightGraphFor(
  measurements: readonly AnalysisWeightMeasurementView[],
  rollingAverages: readonly AnalysisWeightRollingAverageView[],
): SharedGraphView {
  return new SharedGraphView(accessibleDescriptionFor(measurements, rollingAverages), [
    {
      label: 'Measured weight',
      color: '--color-action',
      points: measurements.map((measurement) => ({
        x: measurement.dayNumber,
        y: measurement.weightInKg,
      })),
    },
    ...rollingAverages.map((rollingAverage) => ({
      label: `${rollingAverage.windowInDays}-day rolling average`,
      color: `--color-analysis-weight-rolling-${rollingAverage.windowInDays}`,
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
