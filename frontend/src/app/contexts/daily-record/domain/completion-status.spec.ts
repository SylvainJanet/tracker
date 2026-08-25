import { describe, expect, it } from 'vitest';

import { type CompletionStatus, isCompletionStatus } from './completion-status';

describe('DailyRecord', () => {
  describe('isCompletionStatus', () => {
    it.each(['IN_PROGRESS', 'COMPLETED'] satisfies readonly CompletionStatus[])(
      'accepts %s',
      (status) => {
        expect(isCompletionStatus(status)).toBe(true);
      },
    );

    it.each(['completed', '', 1, null, undefined])('rejects %j', (value) => {
      expect(isCompletionStatus(value)).toBe(false);
    });
  });
});
