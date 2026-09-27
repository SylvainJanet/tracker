import { Directive, input } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import type { EChartsCoreOption } from 'echarts/core';
import { NgxEchartsDirective } from 'ngx-echarts';
import { beforeEach, describe, expect, it } from 'vitest';

import { SharedGraphModel } from '../model/shared.graph.model';
import { SharedGraphMapper } from '../presenter/mapper/shared.graph.mapper';
import { SharedGraphPage } from './shared.graph.page';

@Directive({
  // Test double deliberately mirrors the third-party directive selector.
  // eslint-disable-next-line @angular-eslint/directive-selector
  selector: '[echarts]',
})
class EChartsDirectiveStub {
  readonly options = input<EChartsCoreOption | null>(null);
  readonly autoResize = input(false);
}

describe('SharedGraphPage', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedGraphPage],
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
  });

  it('binds its graph input to a responsive ECharts container', () => {
    const graph = new SharedGraphModel('Measured weight on analysis days 1 through 4.', {
      label: 'Measured weight',
      color: '#2563eb',
      points: [
        { x: 1, y: 82.1 },
        { x: 4, y: 81.9 },
      ],
    });
    const fixture = TestBed.createComponent(SharedGraphPage);

    fixture.componentRef.setInput('graph', graph);
    fixture.detectChanges();

    const chart = fixture.debugElement.query(By.directive(EChartsDirectiveStub));
    const directive = chart?.injector.get(EChartsDirectiveStub);

    expect(chart).not.toBeNull();
    expect(directive?.options()).toEqual(SharedGraphMapper.modelToOptions(graph));
    expect(directive?.autoResize()).toBe(true);
  });
  it('binds its graph input and resolves its shared colour token', () => {
    const graph = new SharedGraphModel('Measured weight on analysis days 1 through 4.', {
      label: 'Measured weight',
      color: '--color-action',
      points: [
        { x: 1, y: 82.1 },
        { x: 4, y: 81.9 },
      ],
    });
    const resolvedGraph = new SharedGraphModel(graph.accessibleDescription, {
      ...graph.series,
      color: '#2563eb',
    });
    const fixture = TestBed.createComponent(SharedGraphPage);

    fixture.nativeElement.style.setProperty('--color-action', '#2563eb');
    fixture.componentRef.setInput('graph', graph);
    fixture.detectChanges();

    const chart = fixture.debugElement.query(By.directive(EChartsDirectiveStub));
    const directive = chart?.injector.get(EChartsDirectiveStub);

    expect(chart).not.toBeNull();
    expect(directive?.options()).toEqual(SharedGraphMapper.modelToOptions(resolvedGraph));
    expect(directive?.autoResize()).toBe(true);
  });
});
