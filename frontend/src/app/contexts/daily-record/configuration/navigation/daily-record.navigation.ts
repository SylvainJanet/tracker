import { defineContextNavigation } from '../../../../shared/api/shared.context-navigation';

export const DAILY_RECORD_PATHS = {
  logging: 'daily-logging',
  summary: 'summary',
} as const;

export const DAILY_RECORD_NAVIGATION = defineContextNavigation({
  defaultPath: DAILY_RECORD_PATHS.logging,
  destinations: [
    {
      path: DAILY_RECORD_PATHS.logging,
      label: 'Daily logging',
      preserveQueryParameters: true,
    },
    {
      path: DAILY_RECORD_PATHS.summary,
      label: 'Summary',
      preserveQueryParameters: true,
    },
  ],
});
