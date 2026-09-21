import { type ContextNavigationModel } from '../adapter/in/web/navigation/model/shared.navigation.model';

export interface SharedConfigurationNavigation extends ContextNavigationModel {
  readonly defaultPath: string;
}

export function defineContextNavigation(
  defaultPath: string,
  contextNavigationModel: ContextNavigationModel,
): SharedConfigurationNavigation {
  if (
    !contextNavigationModel.destinations.some((destination) => destination.path === defaultPath)
  ) {
    throw new Error('The default context navigation path must identify a destination');
  }

  return {
    ...contextNavigationModel,
    defaultPath,
  };
}
