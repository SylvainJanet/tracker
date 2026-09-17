import { isProblemDetailResponseDto } from '../../../../../../shared/api/shared.problem-detail-response';
import type {
  DailyRecordRequestBodyDto,
  DailyRecordResponseDto,
} from './journal-daily-record-http-contract';
import { isWeight } from '../../../../domain/weight';
import { isCalendarDate } from '../../../../domain/calendar-date';
import { dailyRecord, type DailyRecord } from '../../../../domain/daily-record';
import { JournalDailyRecordUnexpectedGatewayError } from '../errors/journal-daily-record-unexpected-gateway.error';

export class JournalDailyRecordHttpContractMapper {
  private constructor() {
    /* empty */
  }

  static createInstructionToRequestBodyDto(instruction: DailyRecord): DailyRecordRequestBodyDto {
    return {
      date: instruction.date,
      weight: instruction.weight,
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

    return typeof candidate['date'] === 'string' && typeof candidate['weight'] === 'number';
  }

  static dailyRecordFromResponseDto(response: unknown): DailyRecord {
    if (
      !JournalDailyRecordHttpContractMapper.isDailyRecordResponseDto(response) ||
      !isWeight(response.weight) ||
      !isCalendarDate(response.date)
    ) {
      throw invalidResponseError();
    }

    return dailyRecord(response.date, response.weight);
  }
}

function invalidResponseError(): JournalDailyRecordUnexpectedGatewayError {
  return new JournalDailyRecordUnexpectedGatewayError(
    'The backend returned an invalid daily record response',
  );
}
