import { type HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';

import { type CalendarDate } from '../../../domain/calendar-date';
import { type DailyRecordResponseDto, DailyRecordUrl } from './contract/daily-record-http-contract';
import {
  translateCreateDailyRecordHttpError,
  translateGetDailyRecordHttpError,
} from './errors/translator/daily-record-http-error.translator';
import { DailyRecordHttpContractMapper } from './contract/daily-record-http-contract.mapper';
import {
  type CreateDailyRecordOutcome,
  type DailyRecordGateway,
  type FindDailyRecordOutcome,
} from '../../../application/port/out/daily-record.gateway';

export class HttpDailyRecordGateway implements DailyRecordGateway {
  constructor(private readonly httpClient: HttpClient) {}

  async findByDate(criteria: CalendarDate): Promise<FindDailyRecordOutcome> {
    try {
      const response = await firstValueFrom(
        this.httpClient.get<DailyRecordResponseDto>(
          DailyRecordUrl.findByDate(
            DailyRecordHttpContractMapper.findByDateCriteriaToRequestParameters(criteria),
          ),
        ),
      );

      return {
        kind: 'found',
        outcomeData: DailyRecordHttpContractMapper.dailyRecordFromResponseDto(response),
      };
    } catch (error: unknown) {
      return translateGetDailyRecordHttpError(error);
    }
  }

  async create(instruction: CalendarDate): Promise<CreateDailyRecordOutcome> {
    try {
      const response = await firstValueFrom(
        this.httpClient.post<DailyRecordResponseDto>(
          DailyRecordUrl.createUrl(),
          DailyRecordHttpContractMapper.createInstructionToRequestBodyDto(instruction),
        ),
      );

      return {
        kind: 'created',
        outcomeData: DailyRecordHttpContractMapper.dailyRecordFromResponseDto(response),
      };
    } catch (error: unknown) {
      return translateCreateDailyRecordHttpError(error);
    }
  }
}
