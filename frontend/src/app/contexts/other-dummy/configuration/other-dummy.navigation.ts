import { defineContextNavigation } from '../../../shared/api/shared.context-navigation';

export const OTHER_DUMMY_PATHS = {
  nothing: 'nothing',
  to: 'to',
  see: 'see',
  here: 'here',
} as const;

export const OTHER_DUMMY_NAVIGATION = defineContextNavigation({
  defaultPath: OTHER_DUMMY_PATHS.nothing,
  destinations: [
    {
      path: OTHER_DUMMY_PATHS.nothing,
      label: 'Nothing',
      preserveQueryParameters: false,
    },
    {
      path: OTHER_DUMMY_PATHS.to,
      label: 'To',
      preserveQueryParameters: false,
    },
    {
      path: OTHER_DUMMY_PATHS.see,
      label: 'See',
      preserveQueryParameters: false,
    },
    {
      path: OTHER_DUMMY_PATHS.here,
      label: 'Here',
      preserveQueryParameters: false,
    },
  ],
});
