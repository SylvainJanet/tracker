import { type NavigationSection } from '../model/navigation.model';
import { APPLICATION_CONTEXTS } from '../../../configuration/contexts.config';

export const NAVIGATION_SECTIONS: readonly NavigationSection[] = APPLICATION_CONTEXTS.map(
  (context) => {
    const contextPath = `/${context.path}`;
    const defaultDestination = context.navigation.destinations.find(
      (destination) => destination.path === context.navigation.defaultPath,
    );

    if (defaultDestination === undefined) {
      throw new Error(
        `Application context "${context.id}" has no destination for its default path`,
      );
    }

    return {
      id: context.id,
      path: contextPath,
      label: context.label,
      preserveQueryParameters: defaultDestination.preserveQueryParameters,
      destinations: context.navigation.destinations.map((destination) => ({
        path: `${contextPath}/${destination.path}`,
        label: destination.label,
        preserveQueryParameters: destination.preserveQueryParameters,
      })),
    };
  },
);
