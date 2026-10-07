import { describe, expect, it } from 'vitest';

import { SharedGraphView } from '../model/view/shared.graph.model.view';
import { SharedGraphPresenter } from './shared.graph.presenter';

describe('SharedGraphPresenter', () => {
  it('starts without graph data', () => {
    const presenter = new SharedGraphPresenter();

    expect(presenter.state()).toEqual({
      dataState: { kind: 'initial' },
    });
  });

  it('presents graph data', () => {
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
    const presenter = new SharedGraphPresenter();

    presenter.setData(graph);

    expect(presenter.state()).toEqual({
      dataState: {
        kind: 'data',
        view: graph,
      },
    });
  });
});
