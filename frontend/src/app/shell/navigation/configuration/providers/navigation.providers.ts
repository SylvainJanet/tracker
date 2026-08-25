import { type Provider } from '@angular/core';

import {
  APPLICATION_NAVIGATION_PRESENTER_FACTORY,
  NavigationPresenter,
} from '../../presenter/navigation.presenter';
import { NAVIGATION_SECTIONS } from '../sections/navigation.sections';

export function provideApplicationNavigation(): Provider {
  return {
    provide: APPLICATION_NAVIGATION_PRESENTER_FACTORY,
    useValue: () => new NavigationPresenter(NAVIGATION_SECTIONS),
  };
}
