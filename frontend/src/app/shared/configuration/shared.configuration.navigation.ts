import { type ContextNavigationModel } from '../adapter/in/web/navigation/model/shared.navigation.model';

export interface SharedConfigurationNavigation extends ContextNavigationModel {
  readonly defaultPath: string;
}

export function defineContextNavigation(
  configuration: SharedConfigurationNavigation,
): SharedConfigurationNavigation {
  if (
    !configuration.destinations.some(
      (destination) => destination.path === configuration.defaultPath,
    )
  ) {
    throw new Error('The default context navigation path must identify a destination');
  }

  return configuration;
}
