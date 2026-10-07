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
});
