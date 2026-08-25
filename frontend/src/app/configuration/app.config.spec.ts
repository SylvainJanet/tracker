import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { describe, expect, it } from 'vitest';

import { AppPage } from '../shell/app/api/page';
import { appConfig } from './app.config';

describe('appConfig', () => {
  it('composes a runnable shell with navigation, HTTP, and default context routing', async () => {
    await TestBed.configureTestingModule({
      imports: [AppPage],
      providers: appConfig.providers,
    }).compileComponents();

    const fixture = TestBed.createComponent(AppPage);
    fixture.detectChanges();

    const router = TestBed.inject(Router);
    await router.navigateByUrl('/');
    await fixture.whenStable();
    fixture.detectChanges();

    expect(router.url).toBe('/daily-record/daily-logging');
    expect(fixture.nativeElement.querySelector('app-navigation-page')).not.toBeNull();
    expect(fixture.nativeElement.querySelector('main router-outlet')).not.toBeNull();
    expect(fixture.nativeElement.querySelector('main h1')?.textContent?.trim()).toBe(
      'Daily logging',
    );
  });
});
