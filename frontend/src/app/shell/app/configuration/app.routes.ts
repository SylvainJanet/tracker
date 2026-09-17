import { type Routes } from '@angular/router';

import {
  APPLICATION_CONTEXTS,
  DEFAULT_APPLICATION_CONTEXT,
} from '../../../configuration/contexts.config';

export const routes: Routes = [
  ...APPLICATION_CONTEXTS.map((context) => ({
    path: context.path,
    loadChildren: context.loadChildren,
  })),
  {
    path: '**',
    redirectTo: DEFAULT_APPLICATION_CONTEXT.path,
  },
];
