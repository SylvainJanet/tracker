import { defineContextNavigation } from '../../../shared/api/shared.context-navigation';

export const JOURNAL_PATHS = {
  logging: 'journal-logging',
  summary: 'summary',
} as const;

export const JOURNAL_NAVIGATION = defineContextNavigation(JOURNAL_PATHS.logging, {
  destinations: [
    {
      path: JOURNAL_PATHS.logging,
      label: 'Daily logging',
      preserveQueryParameters: true,
    },
  ],
});
