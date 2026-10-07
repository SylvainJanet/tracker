import type {
  GetWeightAnalysisResponse,
  RollingAverageResponse,
} from '../contract/response/get-weight-analysis-response';
import type {
  RollingAverageOutcomeData,
  WeightAnalysisOutcomeData,
} from '../../../../application/port/out/weight-analysis.store';

export function toOutcomeData(response: GetWeightAnalysisResponse): WeightAnalysisOutcomeData {
  return {
    timelineStartDate: response.timelineStartDate!,
    range: {
      startDate: response.range!.startDate,
      endDate: response.range!.endDate,
    },
    weightMeasurements: response.weightMeasurements.map((measurement) => ({
      date: measurement.date,
      dayNumber: measurement.dayNumber,
      weightInKg: measurement.weightInKg,
    })),
    rollingAverageSeries: response.rollingAverageSeries.map(toRollingAverageOutcomeData),
  };
}

function toRollingAverageOutcomeData(response: RollingAverageResponse): RollingAverageOutcomeData {
  return {
    windowSize: response.windowSize,
    rollingAverages: response.rollingAverages.map((point) => ({
      date: point.date,
      dayNumber: point.dayNumber,
      includedValues: point.includedValues.map((includedValue) => ({
        date: includedValue.date,
        dayNumber: includedValue.dayNumber,
        weightInKg: includedValue.weightInKg,
      })),
      rollingAverage: {
        exactValue: {
          numerator: point.rollingAverage.exactValue.numerator,
          denominator: point.rollingAverage.exactValue.denominator,
        },
        approximations: point.rollingAverage.approximations.map((approximation) => ({
          value: approximation.value,
          rounding: approximation.rounding,
        })),
      },
    })),
  };
}
