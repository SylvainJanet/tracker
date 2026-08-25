import { describe, expect, it } from 'vitest';

import { defineContextNavigation } from './shared.configuration.navigation';

describe('defineContextNavigation', () => {
  it('preserves a valid context navigation configuration', () => {
    const navigation = defineContextNavigation({
      defaultPath: 'first',
      destinations: [
        {
          path: 'first',
          label: 'First',
          preserveQueryParameters: false,
        },
        {
          path: 'second',
          label: 'Second',
          preserveQueryParameters: true,
        },
      ],
    });

    expect(navigation.defaultPath).toBe('first');
    expect(navigation.destinations).toHaveLength(2);
  });

  it('rejects a default path that does not identify a destination', () => {
    expect(() =>
      defineContextNavigation({
        defaultPath: 'missing',
        destinations: [
          {
            path: 'first',
            label: 'First',
            preserveQueryParameters: false,
          },
        ],
      }),
    ).toThrow('The default context navigation path must identify a destination');
  });
});
