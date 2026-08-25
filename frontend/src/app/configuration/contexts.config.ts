import { type LoadChildren } from '@angular/router';

import { DAILY_RECORD_NAVIGATION } from '../contexts/daily-record/api/navigation';
import { DUMMY_NAVIGATION } from '../contexts/dummy/api/navigation';
import { OTHER_DUMMY_NAVIGATION } from '../contexts/other-dummy/api/navigation';
import type { SharedConfigurationNavigation } from '../shared/api/shared.context-navigation';

interface ApplicationContextRegistration {
  readonly id: string;
  readonly path: string;
  readonly label: string;
  readonly navigation: SharedConfigurationNavigation;
  readonly loadChildren: LoadChildren;
}

const DUMMY_CONTEXT = {
  id: 'dummy',
  path: 'dummy',
  label: 'Dummy',
  navigation: DUMMY_NAVIGATION,
  loadChildren: () => import('../contexts/dummy/api/routes').then((module) => module.DUMMY_ROUTES),
} as const satisfies ApplicationContextRegistration;

const DAILY_RECORD_CONTEXT = {
  id: 'daily-record',
  path: 'daily-record',
  label: 'Daily records',
  navigation: DAILY_RECORD_NAVIGATION,
  loadChildren: () =>
    import('../contexts/daily-record/api/routes').then((module) => module.DAILY_RECORD_ROUTES),
} as const satisfies ApplicationContextRegistration;

const OTHER_DUMMY_CONTEXT = {
  id: 'other-dummy',
  path: 'other-dummy',
  label: 'OtherDummy',
  navigation: OTHER_DUMMY_NAVIGATION,
  loadChildren: () =>
    import('../contexts/other-dummy/api/routes').then((module) => module.OTHER_DUMMY_ROUTES),
} as const satisfies ApplicationContextRegistration;

export const APPLICATION_CONTEXTS = [
  DUMMY_CONTEXT,
  DAILY_RECORD_CONTEXT,
  OTHER_DUMMY_CONTEXT,
] as const satisfies readonly ApplicationContextRegistration[];

export const DEFAULT_APPLICATION_CONTEXT = DAILY_RECORD_CONTEXT;
