import { type Routes } from '@angular/router';

import { OTHER_DUMMY_NAVIGATION, OTHER_DUMMY_PATHS } from '../navigation/other-dummy.navigation';
import { ContextNavigationModel } from '../../../../shared/api/shared.context-navigation';

export const OTHER_DUMMY_ROUTES: Routes = [
  {
    path: '',
    providers: [
      {
        provide: ContextNavigationModel,
        useValue: OTHER_DUMMY_NAVIGATION,
      },
    ],
    loadComponent: () =>
      import('../../adapter/in/web/layout/page/other-dummy.layout.page').then(
        (module) => module.OtherDummyLayoutPage,
      ),
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: OTHER_DUMMY_NAVIGATION.defaultPath,
      },
      {
        path: OTHER_DUMMY_PATHS.nothing,
        loadComponent: () =>
          import('../../adapter/in/web/nothing/page/other-dummy.nothing.page').then(
            (module) => module.OtherDummyNothingPage,
          ),
      },
      {
        path: OTHER_DUMMY_PATHS.to,
        loadComponent: () =>
          import('../../adapter/in/web/to/page/other-dummy.to.page').then(
            (module) => module.OtherDummyToPage,
          ),
      },
      {
        path: OTHER_DUMMY_PATHS.see,
        loadComponent: () =>
          import('../../adapter/in/web/see/page/other-dummy.see.page').then(
            (module) => module.OtherDummySeePage,
          ),
      },
      {
        path: OTHER_DUMMY_PATHS.here,
        loadComponent: () =>
          import('../../adapter/in/web/here/page/other-dummy.here.page').then(
            (module) => module.OtherDummyHerePage,
          ),
      },
    ],
  },
];
