import type { ContextDestination } from '../../../shared/api/shared.context-navigation';
import { type ContextNavigationModel } from '../../../shared/api/shared.context-navigation';

export interface NavigationSection extends ContextDestination, ContextNavigationModel {
  readonly id: string;
}

export type ApplicationNavigationReadModel = Pick<
  NavigationModel,
  'sections' | 'menuOpen' | 'primarySubmenuOpen' | 'sideSubmenuOpen'
>;

export class NavigationModel {
  constructor(
    readonly sections: readonly NavigationSection[],
    readonly menuOpen: boolean,
    private readonly openPrimarySection: string | null,
    private readonly openSideSection: string | null,
  ) {}

  static initial(sections: readonly NavigationSection[]): NavigationModel {
    return new NavigationModel(sections, false, null, null);
  }

  primarySubmenuOpen(sectionId: string): boolean {
    return this.openPrimarySection === sectionId;
  }

  sideSubmenuOpen(sectionId: string, sectionActive: boolean): boolean {
    return sectionActive || this.openSideSection === sectionId;
  }

  openPrimarySubmenu(sectionId: string): NavigationModel {
    if (this.primarySubmenuOpen(sectionId)) {
      return this;
    }

    return new NavigationModel(this.sections, this.menuOpen, sectionId, this.openSideSection);
  }

  closePrimarySubmenu(sectionId: string): NavigationModel {
    if (!this.primarySubmenuOpen(sectionId)) {
      return this;
    }

    return new NavigationModel(this.sections, this.menuOpen, null, this.openSideSection);
  }

  togglePrimarySubmenu(sectionId: string): NavigationModel {
    return this.primarySubmenuOpen(sectionId)
      ? this.closePrimarySubmenu(sectionId)
      : this.openPrimarySubmenu(sectionId);
  }

  openSideSubmenu(sectionId: string): NavigationModel {
    if (this.openSideSection === sectionId) {
      return this;
    }

    return new NavigationModel(this.sections, this.menuOpen, this.openPrimarySection, sectionId);
  }

  closeSideSubmenu(sectionId: string): NavigationModel {
    if (this.openSideSection !== sectionId) {
      return this;
    }

    return new NavigationModel(this.sections, this.menuOpen, this.openPrimarySection, null);
  }

  toggleSideSubmenu(sectionId: string): NavigationModel {
    return this.openSideSection === sectionId
      ? this.closeSideSubmenu(sectionId)
      : this.openSideSubmenu(sectionId);
  }

  openMenu(): NavigationModel {
    if (this.menuOpen) {
      return this;
    }

    return new NavigationModel(this.sections, true, this.openPrimarySection, this.openSideSection);
  }

  closeMenu(): NavigationModel {
    if (!this.menuOpen && this.openSideSection === null) {
      return this;
    }

    return new NavigationModel(this.sections, false, this.openPrimarySection, null);
  }
}
