import { describe, expect, it } from 'vitest';

import { type NavigationSection } from '../model/navigation.model';
import { NavigationPresenter } from './navigation.presenter';

describe('NavigationPresenter', () => {
  const sections = [
    {
      id: 'daily-record',
      path: '/daily-record',
      label: 'Daily records',
      preserveQueryParameters: true,
      destinations: [],
    },
  ] as const satisfies readonly NavigationSection[];

  it('exposes the initial navigation model as a readonly signal', () => {
    const presenter = new NavigationPresenter(sections);

    expect(presenter.model().sections).toBe(sections);
    expect(presenter.model().menuOpen).toBe(false);
    expect(presenter.model().primarySubmenuOpen('daily-record')).toBe(false);
    expect(presenter.model().sideSubmenuOpen('daily-record', false)).toBe(false);
  });

  it('applies primary submenu transitions', () => {
    const presenter = new NavigationPresenter(sections);

    presenter.openPrimarySubmenu('daily-record');

    expect(presenter.model().primarySubmenuOpen('daily-record')).toBe(true);

    presenter.togglePrimarySubmenu('daily-record');

    expect(presenter.model().primarySubmenuOpen('daily-record')).toBe(false);
  });

  it('coordinates menu and side submenu transitions', () => {
    const presenter = new NavigationPresenter(sections);

    presenter.openMenu();
    presenter.openSideSubmenu('daily-record');

    expect(presenter.model().menuOpen).toBe(true);
    expect(presenter.model().sideSubmenuOpen('daily-record', false)).toBe(true);

    presenter.closeMenu();

    expect(presenter.model().menuOpen).toBe(false);
    expect(presenter.model().sideSubmenuOpen('daily-record', false)).toBe(false);
  });

  it('keeps transient state local to each presenter instance', () => {
    const first = new NavigationPresenter(sections);
    const second = new NavigationPresenter(sections);

    first.openMenu();
    first.openPrimarySubmenu('daily-record');

    expect(first.model().menuOpen).toBe(true);
    expect(first.model().primarySubmenuOpen('daily-record')).toBe(true);

    expect(second.model().menuOpen).toBe(false);
    expect(second.model().primarySubmenuOpen('daily-record')).toBe(false);
  });
});
