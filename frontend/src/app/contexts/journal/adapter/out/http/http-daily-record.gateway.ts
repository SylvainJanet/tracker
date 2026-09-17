import { type HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';

import { translateCreateDailyRecordHttpError } from './errors/translator/journal-daily-record-http-error.translator';
import { JournalDailyRecordHttpContractMapper } from './contract/journal-daily-record-http-contract.mapper';
import {
  type CreateDailyRecordOutcome,
  type DailyRecordGateway,
} from '../../../application/port/out/daily-record.gateway';
import type { DailyRecord } from '../../../domain/daily-record';
import { DailyRecordUrl } from './contract/journal-daily-record-http-contract';

export class HttpDailyRecordGateway implements DailyRecordGateway {
  constructor(private readonly httpClient: HttpClient) {}

  async create(instruction: DailyRecord): Promise<CreateDailyRecordOutcome> {
    try {
      const response = await firstValueFrom(
        this.httpClient.post<CreateDailyRecordOutcome>(
          DailyRecordUrl.createUrl(),
          JournalDailyRecordHttpContractMapper.createInstructionToRequestBodyDto(instruction),
        ),
      );

      return {
        kind: 'created',
        outcomeData: JournalDailyRecordHttpContractMapper.dailyRecordFromResponseDto(response),
      };
    } catch (error: unknown) {
      return translateCreateDailyRecordHttpError(error);
    }
  }
}
