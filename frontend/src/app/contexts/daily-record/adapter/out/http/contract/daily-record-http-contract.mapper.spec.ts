import { describe, expect, it } from 'vitest';

import { calendarDate } from '../../../../domain/calendar-date';
import { DailyRecordUnexpectedGatewayError } from '../errors/daily-record-unexpected-gateway.error';
import { DailyRecordHttpContractMapper } from './daily-record-http-contract.mapper';

describe('DailyRecordHttpContractMapper', () => {
  it('maps creation instructions to request bodies', () => {
    const instruction = calendarDate('2026-08-27');

    expect(DailyRecordHttpContractMapper.createInstructionToRequestBodyDto(instruction)).toEqual({
      date: '2026-08-27',
    });
  });

  it('maps lookup criteria to request parameters', () => {
    const criteria = calendarDate('2026-08-27');

    expect(DailyRecordHttpContractMapper.findByDateCriteriaToRequestParameters(criteria)).toEqual({
      date: '2026-08-27',
    });
  });

  it('maps a valid successful response to the domain', () => {
    expect(
      DailyRecordHttpContractMapper.dailyRecordFromResponseDto({
        date: '2026-08-27',
        status: 'COMPLETED',
      }),
    ).toEqual({
      date: '2026-08-27',
      status: 'COMPLETED',
    });
  });

  it('extracts detail only from a valid Problem Details response', () => {
    const response = {
      type: 'https://tracker.test/problems/unavailable',
      title: 'Service unavailable',
      status: 503,
      detail: 'Daily records are temporarily unavailable',
      instance: '/api/daily-records/2026-08-27',
      extension: 'allowed',
    };

    expect(DailyRecordHttpContractMapper.problemDetailMessage(response)).toBe(
      'Daily records are temporarily unavailable',
    );
  });

  it('ignores the detail of a malformed Problem Details response', () => {
    const response = {
      detail: 'must not be trusted without a valid contract',
      status: '503',
    };

    expect(DailyRecordHttpContractMapper.problemDetailMessage(response)).toBeUndefined();
  });

  it('rejects a malformed successful response without exposing its body', () => {
    const response = {
      confidentialPayload: 'must not appear in diagnostics',
    };

    expect(() => DailyRecordHttpContractMapper.dailyRecordFromResponseDto(response)).toThrow(
      expect.objectContaining({
        name: 'DailyRecordUnexpectedGatewayError',
        message: 'The backend returned an invalid daily record response',
      }) satisfies Partial<DailyRecordUnexpectedGatewayError>,
    );
  });

  it.each([
    { date: 'not-a-date', status: 'IN_PROGRESS' },
    { date: '2026-08-27', status: 'UNKNOWN' },
  ])('rejects invalid domain data in a successful response: $date / $status', (response) => {
    expect(() => DailyRecordHttpContractMapper.dailyRecordFromResponseDto(response)).toThrow(
      DailyRecordUnexpectedGatewayError,
    );
  });
});
