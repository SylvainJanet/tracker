import { defineContextNavigation } from '../../../../shared/api/shared.context-navigation';

export const DUMMY_PATHS = {
  foo: 'foo',
  bar: 'bar',
} as const;

export const DUMMY_NAVIGATION = defineContextNavigation({
  defaultPath: DUMMY_PATHS.foo,
  destinations: [
    {
      path: DUMMY_PATHS.foo,
      label: 'Foo',
      preserveQueryParameters: false,
    },
    {
      path: DUMMY_PATHS.bar,
      label: 'Bar',
      preserveQueryParameters: false,
    },
  ],
});
