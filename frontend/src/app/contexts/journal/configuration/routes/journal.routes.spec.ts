import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { JOURNAL_ROUTES } from './journal.routes';

describe('journal routes', () => {
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([
          {
            path: 'journal',
            children: JOURNAL_ROUTES,
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
    const harness = await RouterTestingHarness.create('/journal?date=2026-08-27');
    const renderedContext = harness.routeNativeElement;

    expect(TestBed.inject(Router).url).toBe('/journal/journal-logging?date=2026-08-27');
    expect(renderedContext?.querySelector('nav[aria-label="Daily record"]')).not.toBeNull();
    expect(renderedContext?.querySelector('h1')?.textContent?.trim()).toBe('Daily logging');
    expect(
      Array.from(renderedContext?.querySelectorAll('nav a') ?? [], (link) =>
        link.getAttribute('href'),
      ),
    ).toEqual(['/journal/journal-logging?date=2026-08-27', '/journal/summary?date=2026-08-27']);
  });

  it('renders the summary page under the context layout', async () => {
    const harness = await RouterTestingHarness.create('/journal/summary?date=2026-08-27');

    await harness.fixture.whenStable();
    harness.detectChanges();

    expect(TestBed.inject(Router).url).toBe('/journal/summary?date=2026-08-27');
    expect(harness.routeNativeElement?.querySelector('h1')?.textContent?.trim()).toBe('Summary');
  });
});
