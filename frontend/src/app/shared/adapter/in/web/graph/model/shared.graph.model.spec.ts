import { describe, expect, it } from 'vitest';

import { SharedGraphModel } from './shared.graph.model';
import { SharedGraphView } from './view/shared.graph.model.view';

describe('SharedGraphModel', () => {
  const graph = new SharedGraphView('Measured weight on analysis days 1 through 4.', [
    {
      label: 'Measured weight',
      color: '--color-action',
      points: [
        { x: 1, y: 82.1 },
        { x: 4, y: 81.9 },
      ],
    },
  ]);

  it('starts without graph data', () => {
    expect(SharedGraphModel.initial().state).toEqual({
      dataState: { kind: 'initial' },
    });
  });

  it('accepts its graph data', () => {
    const model = SharedGraphModel.initial().setData(graph);

    expect(model.state).toEqual({
      dataState: {
        kind: 'data',
        view: graph,
      },
    });
  });

  it('rejects replacing graph data', () => {
    const model = SharedGraphModel.initial().setData(graph);
    const replacement = new SharedGraphView('Replacement graph.', []);

    expect(() => model.setData(replacement)).toThrow(
      'Invalid model state transition for transition kind: set-data',
    );
  });
  it('changes, validates and resets an axis interval override', () => {
    const adjustableGraph = new SharedGraphView('Adjustable weight graph.', [], {
      title: 'Weight trends',
      xAxis: {
        label: 'Elapsed day number',
        formatValue: String,
      },
      yAxis: {
        label: 'Weight (kg)',
        formatValue: String,
        policy: {
          kind: 'continuous',
          intervals: [
            { maximumSpanExclusive: 0.2, interval: 0.05 },
            { maximumSpanExclusive: 2, interval: 0.1 },
            { maximumSpanExclusive: 5, interval: 0.5 },
            { maximumSpanExclusive: 20, interval: 1 },
          ],
          fallbackTargetLabelCount: 8,
        },
      },
    });

    const adjusted = SharedGraphModel.initial()
      .setData(adjustableGraph)
      .setAxisInterval('yAxis', 0.1);

    expect(adjusted.state).toEqual({
      dataState: {
        kind: 'data',
        view: adjustableGraph,
        intervalOverrides: {
          yAxis: 0.1,
        },
      },
    });

    expect(adjusted.setAxisInterval('yAxis', 0.25)).toBe(adjusted);

    expect(adjusted.setAxisInterval('yAxis', undefined).state).toEqual({
      dataState: {
        kind: 'data',
        view: adjustableGraph,
        intervalOverrides: {},
      },
    });
  });
});
