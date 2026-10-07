import { Directive, input, signal, type WritableSignal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import type { EChartsCoreOption } from 'echarts/core';
import { NgxEchartsDirective } from 'ngx-echarts';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import type { SharedGraphState } from '../model/state/shared.graph.model.state';
import { SharedGraphView } from '../model/view/shared.graph.model.view';
import {
  SHARED_GRAPH_PRESENTER_FACTORY,
  type SharedGraphPresenter,
  type SharedGraphPresenterFactory,
} from '../presenter/shared.graph.presenter';
import { SharedGraphMapper } from './mapper/shared.graph.mapper';
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
  let presenterState: WritableSignal<SharedGraphState>;
  let setData: ReturnType<typeof vi.fn<SharedGraphPresenter['setData']>>;
  let presenterFactory: ReturnType<typeof vi.fn<SharedGraphPresenterFactory>>;

  beforeEach(async () => {
    presenterState = signal<SharedGraphState>({ dataState: { kind: 'initial' } });
    setData = vi.fn<SharedGraphPresenter['setData']>((view) => {
      presenterState.set({ dataState: { kind: 'data', view } });
    });
    const presenter = {
      state: presenterState.asReadonly(),
      setData,
    } as unknown as SharedGraphPresenter;
    presenterFactory = vi.fn<SharedGraphPresenterFactory>(() => presenter);

    await TestBed.configureTestingModule({
      imports: [SharedGraphPage],
      providers: [
        {
          provide: SHARED_GRAPH_PRESENTER_FACTORY,
          useValue: presenterFactory,
        },
      ],
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

  it('forwards its graph input to its presenter and binds the presented graph', () => {
    const graph = new SharedGraphView('Measured weight on analysis days 1 through 4.', [
      {
        label: 'Measured weight',
        color: '#2563eb',
        points: [
          { x: 1, y: 82.1 },
          { x: 4, y: 81.9 },
        ],
      },
    ]);
    const fixture = TestBed.createComponent(SharedGraphPage);

    fixture.componentRef.setInput('graph', graph);
    fixture.detectChanges();

    const chart = fixture.debugElement.query(By.directive(EChartsDirectiveStub));
    const directive = chart?.injector.get(EChartsDirectiveStub);

    expect(presenterFactory).toHaveBeenCalledOnce();
    expect(setData).toHaveBeenCalledOnce();
    expect(setData).toHaveBeenCalledWith(graph);
    expect(chart).not.toBeNull();
    expect(directive?.options()).toEqual(SharedGraphMapper.modelToOptions(graph));
    expect(directive?.autoResize()).toBe(true);
  });

  it('updates its options from presenter state', () => {
    const inputGraph = new SharedGraphView('Input graph.', [
      {
        label: 'Input series',
        color: '#2563eb',
        points: [{ x: 1, y: 82.1 }],
      },
    ]);
    const presentedGraph = new SharedGraphView('Presented graph.', [
      {
        label: 'Presented series',
        color: '#dc2626',
        points: [{ x: 4, y: 81.9 }],
      },
    ]);
    const fixture = TestBed.createComponent(SharedGraphPage);

    fixture.componentRef.setInput('graph', inputGraph);
    fixture.detectChanges();
    presenterState.set({ dataState: { kind: 'data', view: presentedGraph } });
    fixture.detectChanges();

    const chart = fixture.debugElement.query(By.directive(EChartsDirectiveStub));
    const directive = chart?.injector.get(EChartsDirectiveStub);

    expect(directive?.options()).toEqual(SharedGraphMapper.modelToOptions(presentedGraph));
  });

  it('resolves every series colour token', () => {
    const graph = new SharedGraphView('Measured weight and 7-day rolling average.', [
      {
        label: 'Measured weight',
        color: '--color-action',
        points: [
          { x: 1, y: 82.1 },
          { x: 4, y: 81.9 },
        ],
      },
      {
        label: '7-day rolling average',
        color: '--color-average',
        points: [
          { x: 1, y: 82.1, intensity: 0.5 },
          { x: 4, y: 82, intensity: 1 },
        ],
      },
    ]);
    const resolvedGraph = new SharedGraphView(graph.accessibleDescription, [
      {
        ...graph.series[0]!,
        color: '#2563eb',
      },
      {
        ...graph.series[1]!,
        color: '#dc2626',
      },
    ]);
    const fixture = TestBed.createComponent(SharedGraphPage);

    fixture.nativeElement.style.setProperty('--color-action', '#2563eb');
    fixture.nativeElement.style.setProperty('--color-average', '#dc2626');
    fixture.componentRef.setInput('graph', graph);
    fixture.detectChanges();

    const chart = fixture.debugElement.query(By.directive(EChartsDirectiveStub));
    const directive = chart?.injector.get(EChartsDirectiveStub);

    expect(chart).not.toBeNull();
    expect(directive?.options()).toEqual(SharedGraphMapper.modelToOptions(resolvedGraph));
    expect(directive?.autoResize()).toBe(true);
  });
});
