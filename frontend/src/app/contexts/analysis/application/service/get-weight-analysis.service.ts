import type {
  GetWeightAnalysisResult,
  GetWeightAnalysisUseCase,
} from '../port/in/get-weight-analysis.use-case';
import type { WeightAnalysisStore } from '../port/out/weight-analysis.store';
import { toDomain, toResultData } from './mapper/get-weight-analysis.service.mapper';

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

    const analysis = toDomain(outcome.outcomeData);

    return {
      kind: 'data',
      resultData: toResultData(analysis),
    };
  }
}
