import { afterEach, describe, expect, it, vi } from 'vitest';

import { BrowserTodayProvider } from './browser-today.provider';

describe('BrowserTodayProvider', () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('uses local calendar fields when local midnight crosses a UTC date boundary', () => {
    const now = new Date('2026-01-01T23:30:00.000Z');

    vi.spyOn(now, 'getFullYear').mockReturnValue(2026);
    vi.spyOn(now, 'getMonth').mockReturnValue(0);
    vi.spyOn(now, 'getDate').mockReturnValue(2);

    const provider = new BrowserTodayProvider(() => now);

    expect(now.toISOString().slice(0, 10)).toBe('2026-01-01');
    expect(provider.today()).toBe('2026-01-02');
  });

  it('zero-pads local months and days', () => {
    const now = new Date(0);

    vi.spyOn(now, 'getFullYear').mockReturnValue(2026);
    vi.spyOn(now, 'getMonth').mockReturnValue(8);
    vi.spyOn(now, 'getDate').mockReturnValue(7);

    const provider = new BrowserTodayProvider(() => now);

    expect(provider.today()).toBe('2026-09-07');
  });
});
