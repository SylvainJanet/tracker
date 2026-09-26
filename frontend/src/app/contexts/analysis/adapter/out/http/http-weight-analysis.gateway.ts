import type { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';

import {
  gatewayErrorMessage,
  invalidBackendResponseErrorMessage,
} from '../../../../../shared/api/shared.http.response';
import type {
  GetWeightAnalysisOutcome,
  WeightAnalysisOutcomeData,
  WeightAnalysisStore,
} from '../../../application/port/out/weight-analysis.store';
import { isGetWeightAnalysisResponse } from './contract/response/get-weight-analysis-response';
import { AnalysisHttpContractRoutes } from './contract/routes/analysis-http-contract.routes';

export class HttpWeightAnalysisGateway implements WeightAnalysisStore {
  constructor(private readonly httpClient: HttpClient) {}

  async get(): Promise<GetWeightAnalysisOutcome> {
    try {
      const response = await firstValueFrom(
        this.httpClient.get<unknown>(AnalysisHttpContractRoutes.getWeightAnalysisUrl()),
      );

      if (!isGetWeightAnalysisResponse(response)) {
        return invalidResponse();
      }

      if (response.timelineStartDate === null || response.range === null) {
        return {
          kind: 'empty',
        };
      }

      const outcomeData: WeightAnalysisOutcomeData = {
        timelineStartDate: response.timelineStartDate,
        range: {
          startDate: response.range.startDate,
          endDate: response.range.endDate,
        },
        weightMeasurements: response.weightMeasurements.map((measurement) => ({
          date: measurement.date,
          dayNumber: measurement.dayNumber,
          weightInKg: measurement.weightInKg,
        })),
      };

      return {
        kind: 'data',
        outcomeData,
      };
    } catch (error: unknown) {
      return {
        kind: 'failed',
        outcomeData: {
          errorMessage: gatewayErrorMessage(error),
        },
      };
    }
  }
}

function invalidResponse(): GetWeightAnalysisOutcome {
  return {
    kind: 'failed',
    outcomeData: {
      errorMessage: invalidBackendResponseErrorMessage,
    },
  };
}
