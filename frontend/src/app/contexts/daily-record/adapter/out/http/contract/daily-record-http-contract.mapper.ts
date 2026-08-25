import {
  type CreateDailyRecordRequestBodyDto,
  type DailyRecordResponseDto,
  type GetDailyRecordRequestParameters,
} from './daily-record-http-contract';
import { calendarDate, type CalendarDate, isCalendarDate } from '../../../../domain/calendar-date';
import { type DailyRecord } from '../../../../domain/daily-record';
import { DailyRecordUnexpectedGatewayError } from '../errors/daily-record-unexpected-gateway.error';
import { isProblemDetailResponseDto } from '../../../../../../shared/api/shared.problem-detail-response';
import { isCompletionStatus } from '../../../../domain/completion-status';

export class DailyRecordHttpContractMapper {
  private constructor() {
    /* empty */
  }

  static createInstructionToRequestBodyDto(
    instruction: CalendarDate,
  ): CreateDailyRecordRequestBodyDto {
    return {
      date: instruction,
    };
  }

  static findByDateCriteriaToRequestParameters(
    criteria: CalendarDate,
  ): GetDailyRecordRequestParameters {
    return {
      date: criteria,
    };
  }

  static problemDetailMessage(response: unknown): string | undefined {
    if (!isProblemDetailResponseDto(response)) {
      return undefined;
    }

    return response.detail;
  }

  static isDailyRecordResponseDto(response: unknown): response is DailyRecordResponseDto {
    if (typeof response !== 'object' || response === null) {
      return false;
    }

    const candidate = response as Record<string, unknown>;

    return typeof candidate['date'] === 'string' && typeof candidate['status'] === 'string';
  }

  static dailyRecordFromResponseDto(response: unknown): DailyRecord {
    if (
      !DailyRecordHttpContractMapper.isDailyRecordResponseDto(response) ||
      !isCompletionStatus(response.status) ||
      !isCalendarDate(response.date)
    ) {
      throw invalidResponseError();
    }

    return {
      date: calendarDate(response.date),
      status: response.status,
    };
  }
}

function invalidResponseError(): DailyRecordUnexpectedGatewayError {
  return new DailyRecordUnexpectedGatewayError(
    'The backend returned an invalid daily record response',
  );
}
