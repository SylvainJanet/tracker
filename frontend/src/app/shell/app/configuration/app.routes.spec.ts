import { provideHttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { describe, expect, it } from 'vitest';

import { APPLICATION_CONTEXTS } from '../../../configuration/contexts.config';
import { routes } from './app.routes';

describe('application routes', () => {
  it('navigates between dummy context pages and lets each context choose its default', async () => {
    TestBed.configureTestingModule({
      providers: [provideRouter(routes)],
    });

    const harness = await RouterTestingHarness.create();
    const router = TestBed.inject(Router);
    const dummyPath = registeredContextPath('dummy');
    const otherDummyPath = registeredContextPath('other-dummy');

    await harness.navigateByUrl(dummyPath);
    const dummyContext = harness.routeNativeElement;

    expect(router.url).toBe(`${dummyPath}/foo`);
    expect(dummyContext?.textContent).toContain('Dummy context - Foo page');
    expect(dummyContext?.textContent).toContain('Foo');
    expect(dummyContext?.textContent).toContain('Bar');

    await harness.navigateByUrl(`${otherDummyPath}/here`);
    const otherDummyContext = harness.routeNativeElement;

    expect(router.url).toBe(`${otherDummyPath}/here`);
    expect(otherDummyContext?.textContent).toContain('OtherDummy context - Here page');
    expect(otherDummyContext?.textContent).toContain('Nothing');
    expect(otherDummyContext?.textContent).toContain('To');
    expect(otherDummyContext?.textContent).toContain('See');
    expect(otherDummyContext?.textContent).toContain('Here');
  });

  it('redirects an unknown URL through the default context and its default page', async () => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideRouter(routes)],
    });

    const harness = await RouterTestingHarness.create('/unknown?date=2026-08-27');

    expect(TestBed.inject(Router).url).toBe('/daily-record/daily-logging?date=2026-08-27');
    expect(harness.routeNativeElement?.querySelector('h1')?.textContent?.trim()).toBe(
      'Daily logging',
    );
  });
});

function registeredContextPath(contextId: string): string {
  const context = APPLICATION_CONTEXTS.find(({ id }) => id === contextId);

  if (context === undefined) {
    throw new Error(`Context "${contextId}" is not registered`);
  }

  return `/${context.path}`;
}
