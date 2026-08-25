import { type CalendarDate } from '../../../domain/calendar-date';

export interface TodayProvider {
  today(): CalendarDate;
}
