import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/configuration/app.config';
import { AppPage } from './app/shell/app/api/page';

bootstrapApplication(AppPage, appConfig).catch((err) => console.error(err));
