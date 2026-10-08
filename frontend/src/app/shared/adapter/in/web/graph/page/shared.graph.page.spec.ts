import { Component, Directive, input, output, signal, type WritableSignal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { type ECharts, type EChartsCoreOption } from 'echarts/core';
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
  readonly chartInit = output<ECharts>();
}

@Component({
  selector: 'app-shared-graph-templates-test-host',
  imports: [SharedGraphPage],
  template: `
    <app-shared-graph [graph]="graph" [compact]="compact">
      <ng-template let-x>
        <span data-testid="custom-graph-tooltip">Hovered day {{ x }}</span>
      </ng-template>

      <ng-template #graphNodeDetails let-x let-close="close">
        <section data-testid="custom-graph-node-details">
          Selected day {{ x }}
          <button type="button" data-testid="close-graph-node-details" (click)="close()">
            Close
          </button>
        </section>
      </ng-template>
    </app-shared-graph>
  `,
})
class SharedGraphTemplatesTestHost {
  compact = false;
  readonly graph = new SharedGraphView('Measured weight.', [
    {
      label: 'Measured weight',
      color: '#2563eb',
      points: [
        { x: 1, y: 82.1 },
        { x: 4, y: 81.9 },
      ],
    },
  ]);
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
      imports: [SharedGraphPage, SharedGraphTemplatesTestHost],
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

  it('renders projected tooltip content for the x-axis value selected by ECharts', () => {
    type ChartEventHandler = (event: unknown) => void;

    const handlers = new Map<string, ChartEventHandler>();
    const chart = {
      on: vi.fn((eventName: string, handler: ChartEventHandler) => {
        handlers.set(eventName, handler);
      }),
      off: vi.fn(),
    } as unknown as ECharts;
    const fixture = TestBed.createComponent(SharedGraphTemplatesTestHost);
    fixture.componentInstance.compact = true;

    fixture.detectChanges();

    const chartElement = fixture.debugElement.query(By.directive(EChartsDirectiveStub));
    const directive = chartElement?.injector.get(EChartsDirectiveStub);

    const graphHost = fixture.nativeElement.querySelector('app-shared-graph') as HTMLElement;

    vi.spyOn(graphHost, 'getBoundingClientRect').mockReturnValue({
      x: 500,
      y: 100,
      left: 500,
      top: 100,
      right: 700,
      bottom: 300,
      width: 200,
      height: 200,
      toJSON: () => ({}),
    });

    expect(directive).toBeDefined();

    directive?.chartInit.emit(chart);

    expect(handlers.has('showtip')).toBe(true);
    expect(handlers.has('hidetip')).toBe(true);

    handlers.get('showtip')?.({
      x: 120,
      y: 80,
      dataByCoordSys: [
        {
          dataByAxis: [
            {
              axisDim: 'x',
              value: 4,
            },
          ],
        },
      ],
    });
    fixture.detectChanges();

    const tooltip = fixture.nativeElement.querySelector(
      '[data-testid="shared-graph-tooltip"]',
    ) as HTMLElement | null;

    expect(tooltip).not.toBeNull();
    expect(tooltip?.getAttribute('role')).toBe('tooltip');
    expect(tooltip?.textContent?.trim()).toBe('Hovered day 4');

    expect(tooltip?.classList).toContain('shared-graph__tooltip--compact');
    expect(tooltip?.style.getPropertyValue('--tooltip-viewport-x')).toBe('620px');
    expect(tooltip?.style.getPropertyValue('--tooltip-viewport-y')).toBe('180px');

    handlers.get('hidetip')?.({});
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('[data-testid="shared-graph-tooltip"]')).toBeNull();
  });

  it('disconnects chart events and closes details when replacing or destroying the chart', () => {
    const firstOff = vi.fn();
    const firstChart = {
      on: vi.fn(),
      off: firstOff,
      containPixel: vi.fn(() => true),
      convertFromPixel: vi.fn(() => 4),
    } as unknown as ECharts;

    const secondOff = vi.fn();
    const secondChart = {
      on: vi.fn(),
      off: secondOff,
    } as unknown as ECharts;

    const fixture = TestBed.createComponent(SharedGraphTemplatesTestHost);

    fixture.detectChanges();

    const chartElement = fixture.debugElement.query(By.directive(EChartsDirectiveStub));
    const directive = chartElement?.injector.get(EChartsDirectiveStub);

    directive?.chartInit.emit(firstChart);
    const interaction = fixture.debugElement.query(
      By.css('[data-testid="shared-graph-interaction"]'),
    );
    interaction?.triggerEventHandler('click', {
      offsetX: 120,
      offsetY: 80,
    });
    fixture.detectChanges();

    expect(
      fixture.nativeElement.querySelector('[data-testid="custom-graph-node-details"]'),
    ).not.toBeNull();

    directive?.chartInit.emit(secondChart);
    fixture.detectChanges();

    expect(firstOff).toHaveBeenCalledWith('showtip', expect.any(Function));
    expect(firstOff).toHaveBeenCalledWith('hidetip', expect.any(Function));
    expect(
      fixture.nativeElement.querySelector('[data-testid="custom-graph-node-details"]'),
    ).toBeNull();

    fixture.destroy();

    expect(secondOff).toHaveBeenCalledWith('showtip', expect.any(Function));
    expect(secondOff).toHaveBeenCalledWith('hidetip', expect.any(Function));
  });

  it('binds compact graph options when requested', () => {
    const graph = new SharedGraphView('Compact weight trend.', [
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
    fixture.componentRef.setInput('compact', true);
    fixture.detectChanges();

    const chart = fixture.debugElement.query(By.directive(EChartsDirectiveStub));
    const directive = chart?.injector.get(EChartsDirectiveStub);

    expect(chart?.nativeElement.classList).toContain('shared-graph--compact');
    expect(chart?.nativeElement.classList).not.toContain('shared-graph--interactive');

    expect(directive?.options()).toEqual(
      SharedGraphMapper.modelToOptions(graph, { compact: true }),
    );
  });
  it('opens details for the nearest graph value when clicking inside the plotting grid', () => {
    const chart = {
      on: vi.fn(),
      off: vi.fn(),
      containPixel: vi.fn(() => true),
      convertFromPixel: vi.fn(() => 3),
    } as unknown as ECharts;
    const fixture = TestBed.createComponent(SharedGraphTemplatesTestHost);

    fixture.detectChanges();

    const chartElement = fixture.debugElement.query(By.directive(EChartsDirectiveStub));
    const directive = chartElement?.injector.get(EChartsDirectiveStub);

    directive?.chartInit.emit(chart);
    const interaction = fixture.debugElement.query(
      By.css('[data-testid="shared-graph-interaction"]'),
    );
    interaction?.triggerEventHandler('click', {
      offsetX: 120,
      offsetY: 80,
    });
    fixture.detectChanges();

    expect(chart.containPixel).toHaveBeenCalledWith({ gridIndex: 0 }, [120, 80]);
    expect(chart.convertFromPixel).toHaveBeenCalledWith({ xAxisIndex: 0 }, 120);
    expect(
      fixture.nativeElement.querySelector('[data-testid="custom-graph-node-details"]')?.textContent,
    ).toContain('Selected day 4');

    const closeButton = fixture.nativeElement.querySelector(
      '[data-testid="close-graph-node-details"]',
    ) as HTMLButtonElement;

    closeButton.click();
    fixture.detectChanges();

    expect(
      fixture.nativeElement.querySelector('[data-testid="custom-graph-node-details"]'),
    ).toBeNull();
  });

  it('ignores clicks outside the plotting grid', () => {
    const chart = {
      on: vi.fn(),
      off: vi.fn(),
      containPixel: vi.fn(() => false),
      convertFromPixel: vi.fn(),
    } as unknown as ECharts;
    const fixture = TestBed.createComponent(SharedGraphTemplatesTestHost);

    fixture.detectChanges();

    const chartElement = fixture.debugElement.query(By.directive(EChartsDirectiveStub));
    const directive = chartElement?.injector.get(EChartsDirectiveStub);

    directive?.chartInit.emit(chart);
    const interaction = fixture.debugElement.query(
      By.css('[data-testid="shared-graph-interaction"]'),
    );
    interaction?.triggerEventHandler('click', {
      offsetX: 120,
      offsetY: 280,
    });
    fixture.detectChanges();

    expect(chart.containPixel).toHaveBeenCalledWith({ gridIndex: 0 }, [120, 280]);
    expect(chart.convertFromPixel).not.toHaveBeenCalled();
    expect(
      fixture.nativeElement.querySelector('[data-testid="custom-graph-node-details"]'),
    ).toBeNull();
  });

  it('shows the pointer cursor only while the pointer is inside the plotting grid', () => {
    const containPixel = vi.fn();
    const chart = {
      on: vi.fn(),
      off: vi.fn(),
      containPixel,
    } as unknown as ECharts;
    const fixture = TestBed.createComponent(SharedGraphTemplatesTestHost);

    fixture.detectChanges();

    const chartElement = fixture.debugElement.query(By.directive(EChartsDirectiveStub));
    const directive = chartElement?.injector.get(EChartsDirectiveStub);

    directive?.chartInit.emit(chart);

    containPixel.mockReturnValueOnce(true);
    chartElement?.triggerEventHandler('mousemove', {
      offsetX: 120,
      offsetY: 80,
    });
    fixture.detectChanges();

    expect(chartElement?.nativeElement.classList).toContain('shared-graph--interactive');
    expect(containPixel).toHaveBeenLastCalledWith({ gridIndex: 0 }, [120, 80]);

    containPixel.mockReturnValueOnce(false);
    chartElement?.triggerEventHandler('mousemove', {
      offsetX: 120,
      offsetY: 280,
    });
    fixture.detectChanges();

    expect(chartElement?.nativeElement.classList).not.toContain('shared-graph--interactive');
    expect(containPixel).toHaveBeenLastCalledWith({ gridIndex: 0 }, [120, 280]);
  });

  it('preserves keyboard button semantics when ECharts changes its own host semantics', () => {
    const fixture = TestBed.createComponent(SharedGraphTemplatesTestHost);

    fixture.detectChanges();

    const interaction = fixture.debugElement.query(
      By.css('[data-testid="shared-graph-interaction"]'),
    );
    const chartElement = fixture.debugElement.query(By.directive(EChartsDirectiveStub));

    expect(interaction).not.toBeNull();
    expect(interaction?.nativeElement.getAttribute('role')).toBe('button');
    expect(interaction?.nativeElement.getAttribute('tabindex')).toBe('0');
    expect(interaction?.nativeElement.getAttribute('aria-label')).toContain(
      'Press Enter or Space to open details for the latest value.',
    );
    expect(chartElement?.nativeElement.getAttribute('aria-hidden')).toBe('true');

    chartElement?.nativeElement.setAttribute('role', 'img');
    chartElement?.nativeElement.setAttribute('aria-label', 'ECharts-generated description');

    expect(interaction?.nativeElement.getAttribute('role')).toBe('button');
    expect(interaction?.nativeElement.getAttribute('aria-label')).toContain(
      'Press Enter or Space to open details for the latest value.',
    );

    interaction?.nativeElement.dispatchEvent(
      new KeyboardEvent('keydown', {
        key: 'Enter',
        bubbles: true,
      }),
    );
    fixture.detectChanges();

    expect(
      fixture.nativeElement.querySelector('[data-testid="custom-graph-node-details"]')?.textContent,
    ).toContain('Selected day 4');
  });
});
