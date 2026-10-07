import { Directive, input } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import type { EChartsCoreOption } from 'echarts/core';
import { NgxEchartsDirective } from 'ngx-echarts';
import { describe, expect, it } from 'vitest';

import { SharedGraphPage, SharedGraphView } from '../../../shared/api/shared.graph';
import { appProviders } from './app.providers';

@Directive({
  // Test double deliberately mirrors the third-party directive selector.
  // eslint-disable-next-line @angular-eslint/directive-selector
  selector: '[echarts]',
})
class EChartsDirectiveStub {
  readonly options = input<EChartsCoreOption | null>(null);
  readonly autoResize = input(false);
}

describe('appProviders', () => {
  it('composes a shared graph with its production presenter factory', async () => {
    await TestBed.configureTestingModule({
      imports: [SharedGraphPage],
      providers: appProviders,
    })
      .overrideComponent(SharedGraphPage, {
        remove: {
          imports: [NgxEchartsDirective],
        },
        add: {
          imports: [EChartsDirectiveStub],
        },
      })
      .compileComponents();

    const graph = new SharedGraphView('One measured weight.', [
      {
        label: 'Measured weight',
        color: '#2563eb',
        points: [{ x: 1, y: 82.1 }],
      },
    ]);
    const fixture = TestBed.createComponent(SharedGraphPage);

    fixture.componentRef.setInput('graph', graph);
    fixture.detectChanges();

    const chart = fixture.debugElement.query(By.directive(EChartsDirectiveStub));
    const directive = chart?.injector.get(EChartsDirectiveStub);

    expect(fixture.componentInstance.state()).toEqual({
      dataState: {
        kind: 'data',
        view: graph,
      },
    });
    expect(directive?.options()).toMatchObject({
      aria: {
        enabled: true,
        description: 'One measured weight.',
      },
    });
    expect(directive?.autoResize()).toBe(true);
  });
});
