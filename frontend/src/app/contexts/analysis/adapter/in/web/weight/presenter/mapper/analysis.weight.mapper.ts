import type {
  RollingAveragePointResultData,
  RollingAverageResultData,
  WeightAnalysisResultData,
} from '../../../../../../application/port/in/get-weight-analysis.use-case';
import type {
  AnalysisWeightMeasurementView,
  AnalysisWeightRollingAverageView,
  AnalysisWeightView,
} from '../../model/view/analysis.weight.model.view';
import {
  type SharedGraphRollingAverageTrendView,
  SharedGraphView,
  type SharedRollingAverageGraphDialogView,
  type SharedRollingAverageGraphTooltipView,
} from '../../../../../../../../shared/api/shared.graph';

const MEASURED_WEIGHT_SERIES_LABEL = 'Measured weight';
const MEASURED_WEIGHT_SERIES_COLOR = '--color-action';
const WEIGHT_VALUE_LABEL = 'Weight';

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
      graphDialogByDayNumber: weightGraphDialogByDayNumberFor(resultData),
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

function weightGraphDialogByDayNumberFor(
  resultData: WeightAnalysisResultData,
): AnalysisWeightView['graphDialogByDayNumber'] {
  const dialogByDayNumber: Record<number, SharedRollingAverageGraphDialogView> = {};

  for (const measurement of resultData.weightMeasurements) {
    dialogByDayNumber[measurement.dayNumber] = {
      heading: measurement.date,
      primaryValue: {
        seriesLabel: WEIGHT_VALUE_LABEL,
        color: MEASURED_WEIGHT_SERIES_COLOR,
        formattedValue: formatWeight(measurement.weightInKg),
      },
      rollingAverages: [],
    };
  }

  for (const rollingAverage of resultData.rollingAverageSeries) {
    for (const point of rollingAverage.rollingAverages) {
      const currentDialog = dialogByDayNumber[point.dayNumber] ?? {
        heading: point.date,
        primaryValue: {
          seriesLabel: WEIGHT_VALUE_LABEL,
          color: MEASURED_WEIGHT_SERIES_COLOR,
          formattedValue: 'No measurement',
        },
        rollingAverages: [],
      };

      dialogByDayNumber[point.dayNumber] = {
        ...currentDialog,
        rollingAverages: [
          ...currentDialog.rollingAverages,
          {
            seriesLabel: rollingAverageSeriesLabel(rollingAverage.windowSize),
            color: rollingAverageSeriesColor(rollingAverage.windowSize),
            formattedValue: formatWeight(prettyApproximationFor(point)),
            description: `Average measured weight over the trailing ${rollingAverage.windowSize} calendar days.`,
            coverage: {
              includedValueCount: point.includedValues.length,
              windowInDays: rollingAverage.windowSize,
            },
            calculation: {
              exactValue: {
                numerator: point.rollingAverage.exactValue.numerator,
                denominator: point.rollingAverage.exactValue.denominator,
              },
              prettyApproximation: formatWeight(prettyApproximationFor(point)),
              preciseApproximation: formatWeight(preciseApproximationFor(point)),
            },
            trend: rollingAverageTrendFor(rollingAverage, point),
          },
        ],
      };
    }
  }

  return dialogByDayNumber;
}

function rollingAverageTrendFor(
  rollingAverage: RollingAverageResultData,
  selectedPoint: RollingAveragePointResultData,
): SharedGraphRollingAverageTrendView {
  const firstDayNumber = selectedPoint.dayNumber - rollingAverage.windowSize + 1;
  const rollingPoints = rollingAverage.rollingAverages.filter(
    (point) => point.dayNumber >= firstDayNumber && point.dayNumber <= selectedPoint.dayNumber,
  );
  const measurementByDayNumber = new Map(
    selectedPoint.includedValues.map((measurement) => [measurement.dayNumber, measurement]),
  );
  const tooltipByX: Record<number, SharedRollingAverageGraphTooltipView> = {};

  for (const point of rollingPoints) {
    const measurement = measurementByDayNumber.get(point.dayNumber);

    tooltipByX[point.dayNumber] = {
      heading: point.date,
      primaryValue: {
        seriesLabel: MEASURED_WEIGHT_SERIES_LABEL,
        color: MEASURED_WEIGHT_SERIES_COLOR,
        formattedValue:
          measurement === undefined ? 'No measurement' : formatWeight(measurement.weightInKg),
      },
      rollingAverages: [
        {
          seriesLabel: rollingAverageSeriesLabel(rollingAverage.windowSize),
          color: rollingAverageSeriesColor(rollingAverage.windowSize),
          formattedValue: formatWeight(prettyApproximationFor(point)),
          coverage: {
            includedValueCount: point.includedValues.length,
            windowInDays: rollingAverage.windowSize,
          },
        },
      ],
    };
  }

  const firstDate = rollingPoints[0]?.date ?? selectedPoint.date;
  const lastDate = rollingPoints.at(-1)?.date ?? selectedPoint.date;
  const seriesLabel = rollingAverageSeriesLabel(rollingAverage.windowSize);
  const seriesColor = rollingAverageSeriesColor(rollingAverage.windowSize);

  return {
    graph: new SharedGraphView(
      `Compact line graph of measured weight and the ${seriesLabel} from ${firstDate} to ${lastDate}.`,
      [
        {
          label: MEASURED_WEIGHT_SERIES_LABEL,
          color: MEASURED_WEIGHT_SERIES_COLOR,
          points: selectedPoint.includedValues.map((measurement) => ({
            x: measurement.dayNumber,
            y: measurement.weightInKg,
          })),
        },
        {
          label: seriesLabel,
          color: seriesColor,
          points: rollingPoints.map((point) => ({
            x: point.dayNumber,
            y: prettyApproximationFor(point),
            intensity: rollingAverageIntensity(
              point.includedValues.length,
              rollingAverage.windowSize,
            ),
          })),
        },
      ],
    ),
    tooltipByX,
  };
}

function preciseApproximationFor(point: RollingAveragePointResultData): number {
  const preciseApproximation = point.rollingAverage.approximations.find(
    (approximation) => approximation.rounding === 'PRECISE',
  );

  if (preciseApproximation === undefined) {
    throw new Error('Weight analysis rolling average has no PRECISE approximation.');
  }

  return preciseApproximation.value;
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
