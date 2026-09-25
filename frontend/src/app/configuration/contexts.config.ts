import { type LoadChildren } from '@angular/router';

import { JOURNAL_NAVIGATION } from '../contexts/journal/api/navigation';
import type { SharedConfigurationNavigation } from '../shared/api/shared.context-navigation';
import { ANALYSIS_NAVIGATION } from '../contexts/analysis/api/navigation';

interface ApplicationContextRegistration {
  readonly id: string;
  readonly path: string;
  readonly label: string;
  readonly navigation: SharedConfigurationNavigation;
  readonly loadChildren: LoadChildren;
}

const JOURNAL_CONTEXT = {
  id: 'journal',
  path: 'journal',
  label: 'Journal',
  navigation: JOURNAL_NAVIGATION,
  loadChildren: () =>
    import('../contexts/journal/api/routes').then((module) => module.JOURNAL_ROUTES),
} as const satisfies ApplicationContextRegistration;

const ANALYSIS_CONTEXT = {
  id: 'analysis',
  path: 'analysis',
  label: 'Analysis',
  navigation: ANALYSIS_NAVIGATION,
  loadChildren: () =>
    import('../contexts/analysis/api/routes').then((module) => module.ANALYSIS_ROUTES),
} as const satisfies ApplicationContextRegistration;

export const APPLICATION_CONTEXTS = [
  JOURNAL_CONTEXT,
  ANALYSIS_CONTEXT,
] as const satisfies readonly ApplicationContextRegistration[];

export const DEFAULT_APPLICATION_CONTEXT = JOURNAL_CONTEXT;
