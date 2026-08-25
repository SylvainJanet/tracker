import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { provideRouter, RouterLink, RouterOutlet } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { describe, expect, it } from 'vitest';

import { DailyRecordLayoutPage } from './daily-record.layout.page';
import { ContextNavigationModel } from '../../../../../../../shared/api/shared.context-navigation';

@Component({
  template: '<p>Daily record child page</p>',
})
class TestPage {}

describe('DailyRecordLayoutPage', () => {
  it('renders context-relative navigation and a child under the daily-record mount', async () => {
    await TestBed.configureTestingModule({
      imports: [DailyRecordLayoutPage, TestPage],
      providers: [
        provideRouter([
          {
            path: 'daily-record',
            component: DailyRecordLayoutPage,
            providers: [
              {
                provide: ContextNavigationModel,
                useValue: {
                  destinations: [
                    {
                      path: 'daily-logging',
                      label: 'Daily logging',
                      preserveQueryParameters: true,
                    },
                    {
                      path: 'summary',
                      label: 'Summary',
                      preserveQueryParameters: true,
                    },
                  ],
                } satisfies ContextNavigationModel,
              },
            ],
            children: [
              {
                path: 'daily-logging',
                component: TestPage,
              },
            ],
          },
        ]),
      ],
    }).compileComponents();

    const harness = await RouterTestingHarness.create('/daily-record/daily-logging');
    const layout = harness.routeDebugElement;
    const navigation = layout?.nativeElement.querySelector(
      'nav[aria-label="Daily record"]',
    ) as HTMLElement | null;
    const links = layout?.queryAll(By.directive(RouterLink)) ?? [];

    expect(navigation).not.toBeNull();
    expect(links.map((link) => link.nativeElement.textContent.trim())).toEqual([
      'Daily logging',
      'Summary',
    ]);
    expect(links.map((link) => link.nativeElement.getAttribute('href'))).toEqual([
      '/daily-record/daily-logging',
      '/daily-record/summary',
    ]);

    for (const link of links) {
      expect(link.injector.get(RouterLink).queryParamsHandling).toBe('preserve');
    }

    expect(layout?.query(By.directive(RouterOutlet))).not.toBeNull();
    expect(harness.routeNativeElement?.textContent).toContain('Daily record child page');
  });
});
