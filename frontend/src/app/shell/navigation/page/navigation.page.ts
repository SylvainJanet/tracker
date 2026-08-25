import {
  ChangeDetectionStrategy,
  Component,
  type ElementRef,
  inject,
  ViewChild,
} from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

import {
  APPLICATION_NAVIGATION_PRESENTER_FACTORY,
  NavigationPresenter,
  type ApplicationNavigationPresenterFactory,
} from '../presenter/navigation.presenter';

@Component({
  selector: 'app-navigation-page',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './navigation.page.html',
  styleUrl: './navigation.page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [
    {
      provide: NavigationPresenter,
      useFactory: (factory: ApplicationNavigationPresenterFactory): NavigationPresenter =>
        factory(),
      deps: [APPLICATION_NAVIGATION_PRESENTER_FACTORY],
    },
  ],
})
export class NavigationPage {
  readonly presenter = inject(NavigationPresenter);

  @ViewChild('navigationMenu', { static: true })
  private readonly navigationMenu!: ElementRef<HTMLDialogElement>;

  openPrimarySubmenu(sectionId: string): void {
    this.presenter.openPrimarySubmenu(sectionId);
  }

  closePrimarySubmenu(sectionId: string): void {
    this.presenter.closePrimarySubmenu(sectionId);
  }

  togglePrimarySubmenu(sectionId: string): void {
    this.presenter.togglePrimarySubmenu(sectionId);
  }

  closePrimarySubmenuFromKeyboard(sectionId: string, event: Event): void {
    event.preventDefault();
    this.presenter.closePrimarySubmenu(sectionId);

    const section = event.currentTarget;

    if (section instanceof HTMLElement) {
      section.querySelector<HTMLButtonElement>('.submenu-disclosure')?.focus();
    }
  }

  closePrimarySubmenuAfterFocusLeaves(sectionId: string, event: FocusEvent): void {
    if (!containsRelatedTarget(event)) {
      this.presenter.closePrimarySubmenu(sectionId);
    }
  }

  openSideSubmenu(sectionId: string): void {
    this.presenter.openSideSubmenu(sectionId);
  }

  closeSideSubmenu(sectionId: string): void {
    this.presenter.closeSideSubmenu(sectionId);
  }

  toggleSideSubmenu(sectionId: string): void {
    this.presenter.toggleSideSubmenu(sectionId);
  }

  closeSideSubmenuAfterFocusLeaves(sectionId: string, event: FocusEvent): void {
    if (!containsRelatedTarget(event)) {
      this.presenter.closeSideSubmenu(sectionId);
    }
  }

  openMenu(): void {
    const dialog = this.navigationMenu.nativeElement;

    if (!this.presenter.model().menuOpen && !dialog.open) {
      dialog.showModal();
      this.presenter.openMenu();
    }
  }

  closeMenu(): void {
    const dialog = this.navigationMenu.nativeElement;

    if (dialog.open) {
      dialog.close();
    }

    this.presenter.closeMenu();
  }

  handleMenuClosed(): void {
    this.presenter.closeMenu();
  }
}

function containsRelatedTarget(event: FocusEvent): boolean {
  const container = event.currentTarget;
  const relatedTarget = event.relatedTarget;

  return (
    container instanceof HTMLElement &&
    relatedTarget instanceof Node &&
    container.contains(relatedTarget)
  );
}
