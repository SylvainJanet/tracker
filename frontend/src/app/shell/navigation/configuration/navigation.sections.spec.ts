import { describe, expect, it } from 'vitest';

import { NAVIGATION_SECTIONS } from './navigation.sections';

describe('application navigation configuration', () => {
  it('turns registered context navigation into absolute shell destinations', () => {
    expect(NAVIGATION_SECTIONS).toEqual([
      {
        id: 'dummy',
        path: '/dummy',
        label: 'Dummy',
        preserveQueryParameters: false,
        destinations: [
          { path: '/dummy/foo', label: 'Foo', preserveQueryParameters: false },
          { path: '/dummy/bar', label: 'Bar', preserveQueryParameters: false },
        ],
      },
      {
        id: 'daily-record',
        path: '/daily-record',
        label: 'Daily records',
        preserveQueryParameters: true,
        destinations: [
          {
            path: '/daily-record/daily-logging',
            label: 'Daily logging',
            preserveQueryParameters: true,
          },
          {
            path: '/daily-record/summary',
            label: 'Summary',
            preserveQueryParameters: true,
          },
        ],
      },
      {
        id: 'other-dummy',
        path: '/other-dummy',
        label: 'OtherDummy',
        preserveQueryParameters: false,
        destinations: [
          {
            path: '/other-dummy/nothing',
            label: 'Nothing',
            preserveQueryParameters: false,
          },
          { path: '/other-dummy/to', label: 'To', preserveQueryParameters: false },
          { path: '/other-dummy/see', label: 'See', preserveQueryParameters: false },
          { path: '/other-dummy/here', label: 'Here', preserveQueryParameters: false },
        ],
      },
    ]);
  });
});
