import type { WeightAnalysisOutcomeData } from '../../port/out/weight-analysis.store';
import {
  Analysis,
  Measurement,
  RollingAverage,
  RollingAveragePoint,
} from '../../../domain/weight-analysis';
import { calendarDate, DateRange } from '../../../../../shared/api/shared.calendar';
import type { WeightAnalysisResultData } from '../../port/in/get-weight-analysis.use-case';

export function toDomain(data: WeightAnalysisOutcomeData): Analysis {
  return Analysis.create(
    calendarDate(data.timelineStartDate),
    DateRange.create(calendarDate(data.range.startDate), calendarDate(data.range.endDate)),
    data.weightMeasurements.map((measurement) =>
      Measurement.create(
        calendarDate(measurement.date),
        measurement.dayNumber,
        measurement.weightInKg,
      ),
    ),
    data.rollingAverageSeries.map((rollingAverage) =>
      RollingAverage.create(
        rollingAverage.windowSize,
        rollingAverage.rollingAverages.map((point) =>
          RollingAveragePoint.create(
            calendarDate(point.date),
            point.dayNumber,
            point.includedValues.map((includedValue) =>
              Measurement.create(
                calendarDate(includedValue.date),
                includedValue.dayNumber,
                includedValue.weightInKg,
              ),
            ),
            point.rollingAverage,
          ),
        ),
      ),
    ),
  );
}

export function toResultData(analysis: Analysis): WeightAnalysisResultData {
  return {
    timelineStartDate: analysis.timelineStartDate,
    range: {
      startDate: analysis.range.startDate,
      endDate: analysis.range.endDate,
    },
    weightMeasurements: analysis.measurements.map((measurement) => ({
      date: measurement.date,
      dayNumber: measurement.dayNumber,
      weightInKg: measurement.value,
    })),
    rollingAverageSeries: analysis.rollingAverageSeries.map((rollingAverage) => ({
      windowSize: rollingAverage.windowSize,
      rollingAverages: rollingAverage.rollingAverages.map((point) => ({
        date: point.date,
        dayNumber: point.dayNumber,
        includedValues: point.includedValues.map((includedValue) => ({
          date: includedValue.date,
          dayNumber: includedValue.dayNumber,
          weightInKg: includedValue.value,
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
    })),
  };
}
