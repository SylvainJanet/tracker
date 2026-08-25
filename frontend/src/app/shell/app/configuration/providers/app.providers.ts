import { type ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';

import { routes } from '../routes/app.routes';
import { provideApplicationNavigation } from '../../../navigation/api/providers';

export const appProviders: ApplicationConfig['providers'] = [
  provideBrowserGlobalErrorListeners(),
  provideRouter(routes),
  provideHttpClient(),
  provideApplicationNavigation(),
];
