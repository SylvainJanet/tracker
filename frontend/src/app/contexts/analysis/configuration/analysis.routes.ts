import { type Routes } from '@angular/router';

import { ContextNavigationModel } from '../../../shared/api/shared.context-navigation';
import { ANALYSIS_NAVIGATION, ANALYSIS_PATHS } from './analysis.navigation';
import { provideAnalysisContext, provideAnalysisWeightPresenter } from './analysis.providers';

export const ANALYSIS_ROUTES: Routes = [
  {
    path: '',
    providers: [
      provideAnalysisContext(),
      {
        provide: ContextNavigationModel,
        useValue: ANALYSIS_NAVIGATION,
      },
    ],
    loadComponent: () =>
      import('../adapter/in/web/layout/page/analysis.layout.page').then(
        (module) => module.AnalysisLayoutPage,
      ),
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: ANALYSIS_NAVIGATION.defaultPath,
      },
      {
        path: ANALYSIS_PATHS.weight,
        providers: [provideAnalysisWeightPresenter()],
        loadComponent: () =>
          import('../adapter/in/web/weight/page/analysis.weight.page').then(
            (module) => module.AnalysisWeightPage,
          ),
      },
    ],
  },
];
