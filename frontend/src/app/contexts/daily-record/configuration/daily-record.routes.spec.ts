import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';

import { DAILY_RECORD_ROUTES } from './daily-record.routes';

describe('daily-record routes', () => {
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([
          {
            path: 'daily-record',
            children: DAILY_RECORD_ROUTES,
          },
        ]),
      ],
    });

    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('redirects to logging under the context layout and retains URL state', async () => {
    const harness = await RouterTestingHarness.create('/daily-record?date=2026-08-27');
    const renderedContext = harness.routeNativeElement;

    expect(TestBed.inject(Router).url).toBe('/daily-record/daily-logging?date=2026-08-27');
    expect(renderedContext?.querySelector('nav[aria-label="Daily record"]')).not.toBeNull();
    expect(renderedContext?.querySelector('h1')?.textContent?.trim()).toBe('Daily logging');
    expect(
      Array.from(renderedContext?.querySelectorAll('nav a') ?? [], (link) =>
        link.getAttribute('href'),
      ),
    ).toEqual([
      '/daily-record/daily-logging?date=2026-08-27',
      '/daily-record/summary?date=2026-08-27',
    ]);
  });

  it('renders the summary page under the context layout', async () => {
    const harness = await RouterTestingHarness.create('/daily-record/summary?date=2026-08-27');

    const request = httpTestingController.expectOne('/api/daily-records/2026-08-27');
    request.flush({
      date: '2026-08-27',
      status: 'IN_PROGRESS',
    });

    await harness.fixture.whenStable();
    harness.detectChanges();

    expect(TestBed.inject(Router).url).toBe('/daily-record/summary?date=2026-08-27');
    expect(harness.routeNativeElement?.querySelector('h1')?.textContent?.trim()).toBe('Summary');
    expect(
      harness.routeNativeElement?.querySelector('[data-testid="summary-record"]')?.textContent,
    ).toContain('In progress');
  });
});
