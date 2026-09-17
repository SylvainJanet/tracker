import { type Routes } from '@angular/router';

import { DUMMY_NAVIGATION, DUMMY_PATHS } from './dummy.navigation';
import { ContextNavigationModel } from '../../../shared/api/shared.context-navigation';

export const DUMMY_ROUTES: Routes = [
  {
    path: '',
    providers: [
      {
        provide: ContextNavigationModel,
        useValue: DUMMY_NAVIGATION,
      },
    ],
    loadComponent: () =>
      import('../adapter/in/web/layout/page/dummy.layout.page').then(
        (module) => module.DummyLayoutPage,
      ),
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: DUMMY_NAVIGATION.defaultPath,
      },
      {
        path: DUMMY_PATHS.foo,
        loadComponent: () =>
          import('../adapter/in/web/foo/page/dummy.foo.page').then((module) => module.DummyFooPage),
      },
      {
        path: DUMMY_PATHS.bar,
        loadComponent: () =>
          import('../adapter/in/web/bar/page/dummy.bar.page').then((module) => module.DummyBarPage),
      },
    ],
  },
];
