import { TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { beforeEach, describe, expect, it } from 'vitest';

import { OTHER_DUMMY_ROUTES } from './other-dummy.routes';

describe('other-dummy routes', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter([
          {
            path: 'other-dummy',
            children: OTHER_DUMMY_ROUTES,
          },
        ]),
      ],
    });
  });

  it('redirects to Nothing and renders it under the OtherDummy layout', async () => {
    const harness = await RouterTestingHarness.create('/other-dummy');
    const renderedContext = harness.routeNativeElement;

    expect(TestBed.inject(Router).url).toBe('/other-dummy/nothing');
    expect(renderedContext?.querySelector('nav[aria-label="OtherDummy"]')).not.toBeNull();
    expect(renderedContext?.textContent).toContain('OtherDummy context - Nothing page');
  });

  it.each([
    ['nothing', 'OtherDummy context - Nothing page'],
    ['to', 'OtherDummy context - To page'],
    ['see', 'OtherDummy context - See page'],
    ['here', 'OtherDummy context - Here page'],
  ])('navigates to the %s child page', async (path, expectedText) => {
    const harness = await RouterTestingHarness.create(`/other-dummy/${path}`);

    expect(TestBed.inject(Router).url).toBe(`/other-dummy/${path}`);
    expect(harness.routeNativeElement?.textContent).toContain(expectedText);
  });
});
