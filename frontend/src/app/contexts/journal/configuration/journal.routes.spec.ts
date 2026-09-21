import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { describe, expect, it } from 'vitest';

import { JOURNAL_PATHS } from './journal.navigation';
import { JOURNAL_ROUTES } from './journal.routes';

describe('JOURNAL_ROUTES', () => {
  it('loads the logging page through its production route and providers', async () => {
    await TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter(JOURNAL_ROUTES)],
    }).compileComponents();

    const harness = await RouterTestingHarness.create(`/${JOURNAL_PATHS.logging}`);

    expect(harness.routeNativeElement?.querySelector('app-journal-logging-page')).not.toBeNull();
  });
});
