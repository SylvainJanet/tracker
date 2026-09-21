import type { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import type {
  LogWeightMeasurementInstruction,
  LogWeightMeasurementOutcome,
  LogWeightMeasurementOutcomeData,
  LogWeightMeasurementStore,
} from '../../../application/port/out/log-weight-measurement.store';
import type { LogWeightMeasurementRequest } from './contract/request/log-weight-measurement.request';
import { JournalHttpContractRoutes } from './contract/routes/journal-http-contract.routes';
import type { LogWeightMeasurementResponse } from './contract/response/log-weight-measurement.response';
import { gatewayErrorMessage } from '../../../../../shared/api/shared.http.response';

export class LogWeightMeasurementGateway implements LogWeightMeasurementStore {
  constructor(private readonly httpClient: HttpClient) {}

  async log(instruction: LogWeightMeasurementInstruction): Promise<LogWeightMeasurementOutcome> {
    const request: LogWeightMeasurementRequest = {
      date: instruction.date,
      weightInKg: instruction.weightInKg,
    };

    try {
      const response = await firstValueFrom(
        this.httpClient.post<LogWeightMeasurementResponse>(
          JournalHttpContractRoutes.logWeightMeasurementUrl(),
          request,
        ),
      );

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
          errorMessage: gatewayErrorMessage(error) ?? 'An unexpected gateway error occurred.',
        },
      };
    }
  }
}
