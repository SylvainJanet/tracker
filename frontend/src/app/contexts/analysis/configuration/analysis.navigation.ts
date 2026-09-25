import { defineContextNavigation } from '../../../shared/api/shared.context-navigation';

export const ANALYSIS_PATHS = {
  weight: 'weight',
} as const;

export const ANALYSIS_NAVIGATION = defineContextNavigation(ANALYSIS_PATHS.weight, {
  destinations: [
    {
      path: ANALYSIS_PATHS.weight,
      label: 'Weight',
      preserveQueryParameters: true,
    },
  ],
});
