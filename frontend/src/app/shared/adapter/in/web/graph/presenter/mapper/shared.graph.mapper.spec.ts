import { describe, expect, it } from 'vitest';

import { SharedGraphModel } from '../../model/shared.graph.model';
import { SharedGraphMapper } from './shared.graph.mapper';

describe('SharedGraphMapper', () => {
  it('maps a sparse single-series graph to accessible line-chart options', () => {
    const graph = new SharedGraphModel('Measured weight on analysis days 1 through 4.', {
      label: 'Measured weight',
      color: '#2563eb',
      points: [
        { x: 1, y: 82.1 },
        { x: 4, y: 81.9 },
      ],
    });

    expect(SharedGraphMapper.modelToOptions(graph)).toEqual({
      aria: {
        enabled: true,
        description: 'Measured weight on analysis days 1 through 4.',
      },
      xAxis: {
        type: 'value',
        min: 'dataMin',
        max: 'dataMax',
      },
      yAxis: {
        type: 'value',
        min: 'dataMin',
        max: 'dataMax',
      },
      series: [
        {
          name: 'Measured weight',
          type: 'line',
          data: [
            [1, 82.1],
            [4, 81.9],
          ],
          showSymbol: true,
          lineStyle: {
            color: '#2563eb',
          },
          itemStyle: {
            color: '#2563eb',
          },
        },
      ],
    });
  });
});
