import type { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import type {
  GetWeightMeasurementByDateCriteria,
  GetWeightMeasurementByDateOutcome,
  GetWeightMeasurementByDateOutcomeData,
  LogWeightMeasurementInstruction,
  LogWeightMeasurementOutcome,
  LogWeightMeasurementOutcomeData,
  WeightMeasurementStore,
} from '../../../application/port/out/weight-measurement.store';
import type { LogWeightMeasurementRequest } from './contract/request/log-weight-measurement.request';
import { JournalHttpContractRoutes } from './contract/routes/journal-http-contract.routes';
import { isLogWeightMeasurementResponse } from './contract/response/log-weight-measurement.response';
import {
  gatewayErrorMessage,
  isHttpErrorWithStatus,
} from '../../../../../shared/api/shared.http.response';
import type { GetWeightMeasurementByDateRequest } from './contract/request/get-weight-measurement-by-date-request';
import { isGetWeightMeasurementByDateResponse } from './contract/response/get-weight-measurement-by-date-response';
import { invalidBackendResponseErrorMessage } from '../../../../../shared/api/shared.http.response';

export class WeightMeasurementGateway implements WeightMeasurementStore {
  constructor(private readonly httpClient: HttpClient) {}

  async getByDate(
    criteria: GetWeightMeasurementByDateCriteria,
  ): Promise<GetWeightMeasurementByDateOutcome> {
    const request: GetWeightMeasurementByDateRequest = {
      date: criteria.date,
    };

    try {
      const response = await firstValueFrom(
        this.httpClient.get<unknown>(
          JournalHttpContractRoutes.getWeightMeasurementByDateUrl(request.date),
        ),
      );

      if (!isGetWeightMeasurementByDateResponse(response)) {
        return {
          kind: 'failed',
          outcomeData: {
            errorMessage: invalidBackendResponseErrorMessage,
          },
        };
      }

      if (response.date !== request.date) {
        return {
          kind: 'failed',
          outcomeData: {
            errorMessage: 'The backend returned a weight measurement for a different date.',
          },
        };
      }

      const outcomeData: GetWeightMeasurementByDateOutcomeData = {
        date: response.date,
        weightInKg: response.weightInKg,
      };

      return {
        kind: 'found',
        outcomeData,
      };
    } catch (error: unknown) {
      if (isHttpErrorWithStatus(error, 404)) {
        return {
          kind: 'not-found',
        };
      }
      return {
        kind: 'failed',
        outcomeData: {
          errorMessage: gatewayErrorMessage(error),
        },
      };
    }
  }

  async log(instruction: LogWeightMeasurementInstruction): Promise<LogWeightMeasurementOutcome> {
    const request: LogWeightMeasurementRequest = {
      date: instruction.date,
      weightInKg: instruction.weightInKg,
    };

    try {
      const response = await firstValueFrom(
        this.httpClient.post<unknown>(JournalHttpContractRoutes.logWeightMeasurementUrl(), request),
      );

      if (!isLogWeightMeasurementResponse(response)) {
        return {
          kind: 'failed',
          outcomeData: {
            errorMessage: invalidBackendResponseErrorMessage,
          },
        };
      }

      if (response.date !== request.date) {
        return {
          kind: 'failed',
          outcomeData: {
            errorMessage: 'The backend returned a weight measurement for a different date.',
          },
        };
      }

      const outcomeData: LogWeightMeasurementOutcomeData = {
        date: response.date,
        weightInKg: response.weightInKg,
      };

      return {
        kind: 'logged',
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
