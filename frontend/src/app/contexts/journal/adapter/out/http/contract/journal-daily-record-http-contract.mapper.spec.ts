import { describe, expect, it } from 'vitest';

import { JournalDailyRecordHttpContractMapper } from './journal-daily-record-http-contract.mapper';
import { dailyRecord } from '../../../../domain/daily-record';
import { JournalDailyRecordUnexpectedGatewayError } from '../errors/journal-daily-record-unexpected-gateway.error';

describe('DailyRecordHttpContractMapper', () => {
  it('maps creation instructions to request bodies', () => {
    const instruction = dailyRecord('2026-08-27', 70);

    expect(
      JournalDailyRecordHttpContractMapper.createInstructionToRequestBodyDto(instruction),
    ).toEqual({
      date: '2026-08-27',
      weight: 70,
    });
  });

  it('maps a valid successful response to the domain', () => {
    expect(
      JournalDailyRecordHttpContractMapper.dailyRecordFromResponseDto({
        date: '2026-08-27',
        weight: 70,
      }),
    ).toEqual(dailyRecord('2026-08-27', 70));
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

    expect(JournalDailyRecordHttpContractMapper.problemDetailMessage(response)).toBe(
      'Daily records are temporarily unavailable',
    );
  });

  it('ignores the detail of a malformed Problem Details response', () => {
    const response = {
      detail: 'must not be trusted without a valid contract',
      status: '503',
    };

    expect(JournalDailyRecordHttpContractMapper.problemDetailMessage(response)).toBeUndefined();
  });

  it('rejects a malformed successful response without exposing its body', () => {
    const response = {
      confidentialPayload: 'must not appear in diagnostics',
    };

    expect(() => JournalDailyRecordHttpContractMapper.dailyRecordFromResponseDto(response)).toThrow(
      expect.objectContaining({
        name: 'JournalDailyRecordUnexpectedGatewayError',
        message: 'The backend returned an invalid daily record response',
      }) satisfies Partial<JournalDailyRecordUnexpectedGatewayError>,
    );
  });

  it.each([
    { date: 'not-a-date', weight: 70 },
    { date: '2026-08-27', weight: -1 },
  ])('rejects invalid domain data in a successful response: $date / $weight', (response) => {
    expect(() => JournalDailyRecordHttpContractMapper.dailyRecordFromResponseDto(response)).toThrow(
      JournalDailyRecordUnexpectedGatewayError,
    );
  });
});
