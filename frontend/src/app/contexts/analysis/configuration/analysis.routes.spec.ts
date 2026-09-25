import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { describe, expect, it } from 'vitest';

import { ANALYSIS_PATHS } from './analysis.navigation';
import { ANALYSIS_ROUTES } from './analysis.routes';

describe('ANALYSIS_ROUTES', () => {
  it('loads the weight analysis page through its production route and providers', async () => {
    await TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter(ANALYSIS_ROUTES)],
    }).compileComponents();

    const harness = await RouterTestingHarness.create(`/${ANALYSIS_PATHS.weight}`);

    expect(harness.routeNativeElement?.querySelector('app-analysis-weight-page')).not.toBeNull();
  });
});
