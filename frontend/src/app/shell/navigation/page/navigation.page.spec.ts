import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { provideRouter, Router, RouterLink } from '@angular/router';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { NavigationPage } from './navigation.page';
import { type NavigationSection } from '../model/navigation.model';
import {
  APPLICATION_NAVIGATION_PRESENTER_FACTORY,
  NavigationPresenter,
} from '../presenter/navigation.presenter';

@Component({
  template: '',
})
class TestPage {}

describe('NavigationPage', () => {
  const sections: readonly NavigationSection[] = [
    {
      id: 'daily-record',
      label: 'Daily records',
      path: '/daily-record',
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
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NavigationPage],
      providers: [
        {
          provide: APPLICATION_NAVIGATION_PRESENTER_FACTORY,
          useValue: () => new NavigationPresenter(sections),
        },
        provideRouter([
          {
            path: 'daily-record',
            component: TestPage,
          },
        ]),
      ],
    }).compileComponents();
  });

  it('renders a context and discloses its destinations', () => {
    const fixture = TestBed.createComponent(NavigationPage);

    fixture.detectChanges();

    const section = fixture.nativeElement.querySelector(
      '[data-testid="primary-navigation-section-daily-record"]',
    ) as HTMLElement;
    const sectionLink = section.querySelector('a') as HTMLAnchorElement;
    const disclosure = section.querySelector('button') as HTMLButtonElement;
    const submenu = section.querySelector('ul') as HTMLUListElement;

    expect(sectionLink.textContent?.trim()).toBe('Daily records');
    expect(sectionLink.getAttribute('href')).toBe('/daily-record');
    expect(disclosure.getAttribute('aria-expanded')).toBe('false');
    expect(submenu.hidden).toBe(true);

    section.dispatchEvent(new Event('mouseenter'));
    fixture.detectChanges();

    expect(disclosure.getAttribute('aria-expanded')).toBe('true');
    expect(submenu.hidden).toBe(false);
    expect(Array.from(submenu.querySelectorAll('a'), (link) => link.textContent?.trim())).toEqual([
      'Daily logging',
      'Summary',
    ]);

    const links = fixture.debugElement.queryAll(
      By.css('[data-testid="primary-navigation-section-daily-record"] a'),
    );

    for (const link of links) {
      expect(link.injector.get(RouterLink).queryParamsHandling).toBeNull();
    }

    submenu
      .querySelector('a')
      ?.dispatchEvent(new KeyboardEvent('keydown', { key: 'Escape', bubbles: true }));
    fixture.detectChanges();

    expect(disclosure.getAttribute('aria-expanded')).toBe('false');
    expect(submenu.hidden).toBe(true);
    expect(document.activeElement).toBe(disclosure);
  });

  it('keeps a primary submenu open while focus stays inside its section', () => {
    const fixture = TestBed.createComponent(NavigationPage);
    fixture.detectChanges();

    const section = fixture.nativeElement.querySelector(
      '[data-testid="primary-navigation-section-daily-record"]',
    ) as HTMLElement;
    const sectionLink = section.querySelector('a') as HTMLAnchorElement;
    const disclosure = section.querySelector('button') as HTMLButtonElement;
    const submenu = section.querySelector('ul') as HTMLUListElement;

    sectionLink.dispatchEvent(new FocusEvent('focus'));
    fixture.detectChanges();

    section.dispatchEvent(
      new FocusEvent('focusout', {
        relatedTarget: disclosure,
      }),
    );
    fixture.detectChanges();

    expect(submenu.hidden).toBe(false);

    section.dispatchEvent(
      new FocusEvent('focusout', {
        relatedTarget: document.body,
      }),
    );
    fixture.detectChanges();

    expect(submenu.hidden).toBe(true);
  });

  it('opens the side navigation and discloses context destinations', () => {
    const fixture = TestBed.createComponent(NavigationPage);

    fixture.detectChanges();

    const dialog = fixture.nativeElement.querySelector(
      'dialog#navigation-menu',
    ) as HTMLDialogElement;
    const showModal = vi.fn(() => dialog.setAttribute('open', ''));
    const close = vi.fn(() => dialog.removeAttribute('open'));

    Object.defineProperties(dialog, {
      showModal: {
        configurable: true,
        value: showModal,
      },
      close: {
        configurable: true,
        value: close,
      },
    });

    const menuButton = fixture.nativeElement.querySelector(
      'button[aria-label="Open navigation menu"]',
    ) as HTMLButtonElement;

    menuButton.click();
    fixture.detectChanges();

    expect(showModal).toHaveBeenCalledOnce();
    expect(menuButton.getAttribute('aria-expanded')).toBe('true');

    const section = dialog.querySelector(
      '[data-testid="side-navigation-section-daily-record"]',
    ) as HTMLElement;
    const disclosure = section.querySelector('button') as HTMLButtonElement;
    const submenuRegion = section.querySelector('.side-navigation-submenu-region') as HTMLElement;
    const submenu = section.querySelector('ul') as HTMLUListElement;

    expect(disclosure.getAttribute('aria-expanded')).toBe('false');
    expect(submenuRegion.classList).toContain('side-navigation-submenu-region-flyout');
    expect(submenuRegion.contains(submenu)).toBe(true);
    expect(submenuRegion.hidden).toBe(true);

    section.dispatchEvent(new Event('mouseenter'));
    fixture.detectChanges();

    expect(disclosure.getAttribute('aria-expanded')).toBe('true');
    expect(submenuRegion.hidden).toBe(false);

    const closeButton = dialog.querySelector(
      'button[aria-label="Close navigation menu"]',
    ) as HTMLButtonElement;

    closeButton.click();
    fixture.detectChanges();

    expect(close).toHaveBeenCalledOnce();
    expect(menuButton.getAttribute('aria-expanded')).toBe('false');
  });

  it('synchronizes presenter state when the browser closes the navigation dialog', () => {
    const fixture = TestBed.createComponent(NavigationPage);
    fixture.detectChanges();

    const dialog = fixture.nativeElement.querySelector(
      'dialog#navigation-menu',
    ) as HTMLDialogElement;
    const showModal = vi.fn(() => dialog.setAttribute('open', ''));

    Object.defineProperty(dialog, 'showModal', {
      configurable: true,
      value: showModal,
    });

    const menuButton = fixture.nativeElement.querySelector(
      'button[aria-label="Open navigation menu"]',
    ) as HTMLButtonElement;

    menuButton.click();
    fixture.detectChanges();

    expect(menuButton.getAttribute('aria-expanded')).toBe('true');

    dialog.removeAttribute('open');
    dialog.dispatchEvent(new Event('close'));
    fixture.detectChanges();

    expect(menuButton.getAttribute('aria-expanded')).toBe('false');
  });

  it('shows the active context destinations inline in the side navigation', async () => {
    const fixture = TestBed.createComponent(NavigationPage);

    fixture.detectChanges();

    await TestBed.inject(Router).navigateByUrl('/daily-record');
    fixture.detectChanges();

    const section = fixture.nativeElement.querySelector(
      '[data-testid="side-navigation-section-daily-record"]',
    ) as HTMLElement;
    const submenuRegion = section.querySelector('.side-navigation-submenu-region') as HTMLElement;
    const submenu = section.querySelector('ul') as HTMLUListElement;

    expect(section.classList).toContain('active');
    expect(submenuRegion.classList).not.toContain('side-navigation-submenu-region-flyout');
    expect(submenuRegion.hidden).toBe(false);
    expect(submenu.classList).toContain('side-navigation-submenu-inline');

    const links = fixture.debugElement.queryAll(
      By.css('[data-testid="side-navigation-section-daily-record"] a'),
    );

    for (const link of links) {
      expect(link.injector.get(RouterLink).queryParamsHandling).toBe('preserve');
    }
  });
});
