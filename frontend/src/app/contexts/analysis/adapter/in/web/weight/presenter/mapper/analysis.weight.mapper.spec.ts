import { describe, expect, it } from 'vitest';

import type { WeightAnalysisResultData } from '../../../../../../application/port/in/get-weight-analysis.use-case';
import { AnalysisWeightMapper } from './analysis.weight.mapper';

describe('AnalysisWeightMapper', () => {
  it('maps weight analysis result data to a presentation view', () => {
    const resultData: WeightAnalysisResultData = {
      timelineStartDate: '2026-09-20',
      range: {
        startDate: '2026-09-20',
        endDate: '2026-09-25',
      },
      weightMeasurements: [
        {
          date: '2026-09-20',
          dayNumber: 1,
          weightInKg: 82.1,
        },
        {
          date: '2026-09-23',
          dayNumber: 4,
          weightInKg: 81.9,
        },
      ],
    };

    expect(AnalysisWeightMapper.resultDataToView(resultData)).toEqual({
      timelineStartDate: '2026-09-20',
      range: {
        startDate: '2026-09-20',
        endDate: '2026-09-25',
      },
      weightMeasurements: [
        {
          date: '2026-09-20',
          dayNumber: 1,
          weightInKg: 82.1,
        },
        {
          date: '2026-09-23',
          dayNumber: 4,
          weightInKg: 81.9,
        },
      ],
    });
  });
});
