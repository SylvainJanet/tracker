import { InjectionToken, type Signal, signal, type WritableSignal } from '@angular/core';

import {
  NavigationModel,
  type ApplicationNavigationReadModel,
  type NavigationSection,
} from '../model/navigation.model';

export class NavigationPresenter {
  private readonly currentModel: WritableSignal<NavigationModel>;

  readonly model: Signal<ApplicationNavigationReadModel>;

  constructor(sections: readonly NavigationSection[]) {
    this.currentModel = signal(NavigationModel.initial(sections));
    this.model = this.currentModel.asReadonly();
  }

  openPrimarySubmenu(sectionId: string): void {
    this.currentModel.update((model) => model.openPrimarySubmenu(sectionId));
  }

  closePrimarySubmenu(sectionId: string): void {
    this.currentModel.update((model) => model.closePrimarySubmenu(sectionId));
  }

  togglePrimarySubmenu(sectionId: string): void {
    this.currentModel.update((model) => model.togglePrimarySubmenu(sectionId));
  }

  openSideSubmenu(sectionId: string): void {
    this.currentModel.update((model) => model.openSideSubmenu(sectionId));
  }

  closeSideSubmenu(sectionId: string): void {
    this.currentModel.update((model) => model.closeSideSubmenu(sectionId));
  }

  toggleSideSubmenu(sectionId: string): void {
    this.currentModel.update((model) => model.toggleSideSubmenu(sectionId));
  }

  openMenu(): void {
    this.currentModel.update((model) => model.openMenu());
  }

  closeMenu(): void {
    this.currentModel.update((model) => model.closeMenu());
  }
}

export type ApplicationNavigationPresenterFactory = () => NavigationPresenter;

export const APPLICATION_NAVIGATION_PRESENTER_FACTORY =
  new InjectionToken<ApplicationNavigationPresenterFactory>(
    'ApplicationNavigationPresenterFactory',
  );
