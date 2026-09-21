import { type Routes } from '@angular/router';

import { ContextNavigationModel } from '../../../shared/api/shared.context-navigation';
import { provideJournalContext, provideJournalLoggingPresenter } from './journal.providers';
import { JOURNAL_NAVIGATION, JOURNAL_PATHS } from './journal.navigation';

export const JOURNAL_ROUTES: Routes = [
  {
    path: '',
    providers: [
      provideJournalContext(),
      {
        provide: ContextNavigationModel,
        useValue: JOURNAL_NAVIGATION,
      },
    ],
    loadComponent: () =>
      import('../adapter/in/web/layout/page/journal.layout.page').then(
        (module) => module.JournalLayoutPage,
      ),
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: JOURNAL_NAVIGATION.defaultPath,
      },
      {
        path: JOURNAL_PATHS.logging,
        providers: [provideJournalLoggingPresenter()],
        loadComponent: () =>
          import('../adapter/in/web/logging/page/journal.logging.page').then(
            (module) => module.JournalLoggingPage,
          ),
      },
    ],
  },
];
