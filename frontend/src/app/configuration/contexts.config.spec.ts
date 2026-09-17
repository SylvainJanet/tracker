import { describe, expect, it } from 'vitest';

import { APPLICATION_CONTEXTS, DEFAULT_APPLICATION_CONTEXT } from './contexts.config';

describe('application context registry', () => {
  it('registers contexts in display order and chooses Daily Records as the default', () => {
    expect(
      APPLICATION_CONTEXTS.map(({ id, path, label }) => ({
        id,
        path,
        label,
      })),
    ).toEqual([
      {
        id: 'dummy',
        path: 'dummy',
        label: 'Dummy',
      },
      {
        id: 'journal',
        path: 'journal',
        label: 'Journal',
      },
      {
        id: 'other-dummy',
        path: 'other-dummy',
        label: 'OtherDummy',
      },
    ]);

    expect(DEFAULT_APPLICATION_CONTEXT).toBe(APPLICATION_CONTEXTS[1]);
  });

  it('associates each context with its complete relative navigation model', () => {
    expect(APPLICATION_CONTEXTS.map((context) => context.navigation)).toEqual([
      {
        defaultPath: 'foo',
        destinations: [
          { path: 'foo', label: 'Foo', preserveQueryParameters: false },
          { path: 'bar', label: 'Bar', preserveQueryParameters: false },
        ],
      },
      {
        defaultPath: 'journal-logging',
        destinations: [
          {
            path: 'journal-logging',
            label: 'Daily logging',
            preserveQueryParameters: true,
          },
          { path: 'summary', label: 'Summary', preserveQueryParameters: true },
        ],
      },
      {
        defaultPath: 'nothing',
        destinations: [
          { path: 'nothing', label: 'Nothing', preserveQueryParameters: false },
          { path: 'to', label: 'To', preserveQueryParameters: false },
          { path: 'see', label: 'See', preserveQueryParameters: false },
          { path: 'here', label: 'Here', preserveQueryParameters: false },
        ],
      },
    ]);
  });

  it('derives context-level navigation from the default destination', () => {
    for (const context of APPLICATION_CONTEXTS) {
      expect(context).not.toHaveProperty('preserveQueryParameters');

      expect(
        context.navigation.destinations.some(
          (destination) => destination.path === context.navigation.defaultPath,
        ),
      ).toBe(true);
    }
  });
});
