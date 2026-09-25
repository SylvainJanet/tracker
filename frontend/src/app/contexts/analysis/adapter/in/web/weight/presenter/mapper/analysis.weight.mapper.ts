import type { WeightAnalysisResultData } from '../../../../../../application/port/in/get-weight-analysis.use-case';
import type { AnalysisWeightView } from '../../model/view/analysis.weight.model.view';

export class AnalysisWeightMapper {
  private constructor() {
    /* empty */
  }

  static resultDataToView(resultData: WeightAnalysisResultData): AnalysisWeightView {
    return {
      timelineStartDate: resultData.timelineStartDate,
      range: {
        startDate: resultData.range.startDate,
        endDate: resultData.range.endDate,
      },
      weightMeasurements: resultData.weightMeasurements.map((measurement) => ({
        date: measurement.date,
        dayNumber: measurement.dayNumber,
        weightInKg: measurement.weightInKg,
      })),
    };
  }
}
