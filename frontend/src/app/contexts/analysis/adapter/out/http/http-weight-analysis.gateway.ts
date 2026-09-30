import type { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';

import {
  gatewayErrorMessage,
  invalidBackendResponseErrorMessage,
} from '../../../../../shared/api/shared.http.response';
import type {
  GetWeightAnalysisOutcome,
  RollingAverageOutcomeData,
  WeightAnalysisOutcomeData,
  WeightAnalysisStore,
} from '../../../application/port/out/weight-analysis.store';
import {
  isGetWeightAnalysisResponse,
  type RollingAverageResponse,
} from './contract/response/get-weight-analysis-response';
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
        rollingAverages: response.rollingAverageSeries.map(toRollingAverageOutcomeData),
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

function toRollingAverageOutcomeData(response: RollingAverageResponse): RollingAverageOutcomeData {
  return {
    windowInDays: response.windowSize,
    points: response.rollingAverages.map((point) => ({
      date: point.date,
      dayNumber: point.dayNumber,
      includedValues: point.includedValues.map((includedValue) => ({
        date: includedValue.date,
        dayNumber: includedValue.dayNumber,
        weightInKg: includedValue.weightInKg,
      })),
      rollingAverage: {
        exactValue: {
          numerator: point.rollingAverage.exactValue.numerator,
          denominator: point.rollingAverage.exactValue.denominator,
        },
        approximations: point.rollingAverage.approximations.map((approximation) => ({
          value: approximation.value,
          rounding: approximation.rounding,
        })),
      },
    })),
  };
}

function invalidResponse(): GetWeightAnalysisOutcome {
  return {
    kind: 'failed',
    outcomeData: {
      errorMessage: invalidBackendResponseErrorMessage,
    },
  };
}
