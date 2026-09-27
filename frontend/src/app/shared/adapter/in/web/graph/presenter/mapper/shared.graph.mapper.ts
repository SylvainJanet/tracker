import type { EChartsCoreOption } from 'echarts/core';

import type { SharedGraphModel } from '../../model/shared.graph.model';

export class SharedGraphMapper {
  private constructor() {
    /* empty */
  }

  static modelToOptions(model: SharedGraphModel): EChartsCoreOption {
    const { series } = model;

    return {
      aria: {
        enabled: true,
        description: model.accessibleDescription,
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
          name: series.label,
          type: 'line',
          data: series.points.map((point) => [point.x, point.y]),
          showSymbol: true,
          lineStyle: {
            color: series.color,
          },
          itemStyle: {
            color: series.color,
          },
        },
      ],
    };
  }
}
