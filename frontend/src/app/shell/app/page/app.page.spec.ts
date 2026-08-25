import { TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { provideRouter, RouterOutlet } from '@angular/router';
import { beforeEach, describe, expect, it } from 'vitest';

import { NavigationPage } from '../../navigation/api/page';
import { type NavigationSection } from '../../navigation/model/navigation.model';
import {
  APPLICATION_NAVIGATION_PRESENTER_FACTORY,
  NavigationPresenter,
} from '../../navigation/presenter/navigation.presenter';
import { AppPage } from './app.page';

describe('AppPage', () => {
  const sections = [
    {
      id: 'test-context',
      label: 'Test context',
      path: '/test-context',
      preserveQueryParameters: false,
      destinations: [],
    },
  ] as const satisfies readonly NavigationSection[];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppPage],
      providers: [
        provideRouter([]),
        {
          provide: APPLICATION_NAVIGATION_PRESENTER_FACTORY,
          useValue: () => new NavigationPresenter(sections),
        },
      ],
    }).compileComponents();
  });

  it('composes the navigation page and the application router outlet', () => {
    const fixture = TestBed.createComponent(AppPage);
    fixture.detectChanges();

    expect(fixture.debugElement.query(By.directive(NavigationPage))).not.toBeNull();
    expect(fixture.debugElement.query(By.directive(RouterOutlet))).not.toBeNull();
    expect(fixture.nativeElement.textContent).toContain('Test context');
  });
});
