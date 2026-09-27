import { SharedGraphModel } from '../../../../../../../../shared/api/shared.graph';
import type { WeightAnalysisResultData } from '../../../../../../application/port/in/get-weight-analysis.use-case';
import type {
  AnalysisWeightMeasurementView,
  AnalysisWeightView,
} from '../../model/view/analysis.weight.model.view';

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

    return {
      timelineStartDate: resultData.timelineStartDate,
      range: {
        startDate: resultData.range.startDate,
        endDate: resultData.range.endDate,
      },
      weightMeasurements,
      graph: weightGraphFor(weightMeasurements),
    };
  }
}

function weightGraphFor(measurements: readonly AnalysisWeightMeasurementView[]): SharedGraphModel {
  return new SharedGraphModel(accessibleDescriptionFor(measurements), {
    label: 'Measured weight',
    color: '--color-action',
    points: measurements.map((measurement) => ({
      x: measurement.dayNumber,
      y: measurement.weightInKg,
    })),
  });
}

function accessibleDescriptionFor(measurements: readonly AnalysisWeightMeasurementView[]): string {
  const firstMeasurement = measurements[0];
  const lastMeasurement = measurements.at(-1);

  if (firstMeasurement === undefined || lastMeasurement === undefined) {
    return 'Line graph with no measured weights.';
  }

  if (measurements.length === 1) {
    return `Line graph of 1 measured weight on analysis day ${firstMeasurement.dayNumber}.`;
  }

  return `Line graph of ${measurements.length} measured weights from analysis day ${firstMeasurement.dayNumber} to analysis day ${lastMeasurement.dayNumber}.`;
}
