import type {
  GetWeightAnalysisResult,
  GetWeightAnalysisUseCase,
  WeightAnalysisResultData,
} from '../port/in/get-weight-analysis.use-case';
import type { WeightAnalysisStore } from '../port/out/weight-analysis.store';
import { WeightAnalysis } from '../../domain/weight-analysis';

export class GetWeightAnalysisService implements GetWeightAnalysisUseCase {
  constructor(private readonly store: WeightAnalysisStore) {}

  async get(): Promise<GetWeightAnalysisResult> {
    const outcome = await this.store.get();

    if (outcome.kind === 'failed') {
      throw new Error('Failed to get weight analysis: ' + outcome.outcomeData.errorMessage);
    }

    if (outcome.kind === 'empty') {
      return {
        kind: 'empty',
      };
    }

    const analysis = WeightAnalysis.create(outcome.outcomeData);

    return {
      kind: 'data',
      resultData: toResultData(analysis),
    };
  }
}

function toResultData(analysis: WeightAnalysis): WeightAnalysisResultData {
  return {
    timelineStartDate: analysis.timelineStartDate,
    range: {
      startDate: analysis.range.startDate,
      endDate: analysis.range.endDate,
    },
    weightMeasurements: analysis.weightMeasurements.map((measurement) => ({
      date: measurement.calendarDate,
      dayNumber: measurement.dayNumber,
      weightInKg: measurement.weightInKilograms(),
    })),
  };
}
