import { TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { beforeEach, describe, expect, it } from 'vitest';

import { DUMMY_ROUTES } from './dummy.routes';

describe('dummy routes', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter([
          {
            path: 'dummy',
            children: DUMMY_ROUTES,
          },
        ]),
      ],
    });
  });

  it('redirects to Foo and renders it under the Dummy layout', async () => {
    const harness = await RouterTestingHarness.create('/dummy');
    const renderedContext = harness.routeNativeElement;

    expect(TestBed.inject(Router).url).toBe('/dummy/foo');
    expect(renderedContext?.querySelector('nav[aria-label="Dummy"]')).not.toBeNull();
    expect(renderedContext?.textContent).toContain('Dummy context - Foo page');
  });

  it.each([
    ['foo', 'Dummy context - Foo page'],
    ['bar', 'Dummy context - Bar page'],
  ])('navigates to the %s child page', async (path, expectedText) => {
    const harness = await RouterTestingHarness.create(`/dummy/${path}`);

    expect(TestBed.inject(Router).url).toBe(`/dummy/${path}`);
    expect(harness.routeNativeElement?.textContent).toContain(expectedText);
  });
});
