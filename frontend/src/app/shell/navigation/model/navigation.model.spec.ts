import { describe, expect, it } from 'vitest';
import { NavigationModel, type NavigationSection } from './navigation.model';

describe('NavigationModel', () => {
  const sections = [
    {
      id: 'daily-record',
      path: '/daily-record',
      label: 'Daily records',
      preserveQueryParameters: true,
      destinations: [],
    },
    {
      id: 'dummy',
      path: '/dummy',
      label: 'Dummy',
      preserveQueryParameters: false,
      destinations: [],
    },
  ] as const satisfies readonly NavigationSection[];

  it('starts with all transient navigation state closed', () => {
    const model = NavigationModel.initial(sections);

    expect(model.sections).toBe(sections);
    expect(model.menuOpen).toBe(false);
    expect(model.primarySubmenuOpen('daily-record')).toBe(false);
    expect(model.sideSubmenuOpen('daily-record', false)).toBe(false);
    expect(model.sideSubmenuOpen('daily-record', true)).toBe(true);
  });

  it('transitions primary submenu state immutably', () => {
    const initial = NavigationModel.initial(sections);

    const dailyRecordOpen = initial.openPrimarySubmenu('daily-record');

    expect(initial.primarySubmenuOpen('daily-record')).toBe(false);
    expect(dailyRecordOpen.primarySubmenuOpen('daily-record')).toBe(true);

    const dummyOpen = dailyRecordOpen.openPrimarySubmenu('dummy');

    expect(dummyOpen.primarySubmenuOpen('daily-record')).toBe(false);
    expect(dummyOpen.primarySubmenuOpen('dummy')).toBe(true);
    expect(dummyOpen.closePrimarySubmenu('daily-record')).toBe(dummyOpen);

    const closed = dummyOpen.togglePrimarySubmenu('dummy');

    expect(closed.primarySubmenuOpen('dummy')).toBe(false);
  });

  it('coordinates the dialog and side submenu state', () => {
    const initial = NavigationModel.initial(sections);

    const open = initial.openMenu().openSideSubmenu('daily-record');

    expect(initial.menuOpen).toBe(false);
    expect(open.menuOpen).toBe(true);
    expect(open.sideSubmenuOpen('daily-record', false)).toBe(true);

    const toggled = open.toggleSideSubmenu('daily-record');

    expect(toggled.sideSubmenuOpen('daily-record', false)).toBe(false);

    const closed = open.closeMenu();

    expect(closed.menuOpen).toBe(false);
    expect(closed.sideSubmenuOpen('daily-record', false)).toBe(false);
  });
});
