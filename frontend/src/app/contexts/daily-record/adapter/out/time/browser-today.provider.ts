import { type TodayProvider } from '../../../application/port/out/today.provider';
import { type CalendarDate, calendarDate } from '../../../domain/calendar-date';

export class BrowserTodayProvider implements TodayProvider {
  constructor(private readonly now: () => Date = () => new Date()) {}

  today(): CalendarDate {
    const now = this.now();

    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');

    return calendarDate(`${year}-${month}-${day}`);
  }
}
