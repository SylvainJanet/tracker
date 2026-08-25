import { type Routes } from '@angular/router';
import {
  provideDailyRecordLoggingPresenter,
  provideDailyRecordContext,
  provideDailyRecordSummaryPresenter,
} from '../providers/daily-record.providers';
import { DAILY_RECORD_NAVIGATION, DAILY_RECORD_PATHS } from '../navigation/daily-record.navigation';
import { ContextNavigationModel } from '../../../../shared/api/shared.context-navigation';

export const DAILY_RECORD_ROUTES: Routes = [
  {
    path: '',
    providers: [
      provideDailyRecordContext(),
      {
        provide: ContextNavigationModel,
        useValue: DAILY_RECORD_NAVIGATION,
      },
    ],
    loadComponent: () =>
      import('../../adapter/in/web/layout/page/daily-record.layout.page').then(
        (module) => module.DailyRecordLayoutPage,
      ),
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: DAILY_RECORD_NAVIGATION.defaultPath,
      },
      {
        path: DAILY_RECORD_PATHS.logging,
        providers: [provideDailyRecordLoggingPresenter()],
        loadComponent: () =>
          import('../../adapter/in/web/logging/page/daily-record.logging.page').then(
            (module) => module.DailyRecordLoggingPage,
          ),
      },
      {
        path: DAILY_RECORD_PATHS.summary,
        providers: [provideDailyRecordSummaryPresenter()],
        loadComponent: () =>
          import('../../adapter/in/web/summary/page/daily-record.summary.page').then(
            (module) => module.DailyRecordSummaryPage,
          ),
      },
    ],
  },
];
