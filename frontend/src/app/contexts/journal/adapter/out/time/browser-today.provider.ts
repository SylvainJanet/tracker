import {
  type TodayOutcome,
  type TodayProvider,
} from '../../../application/port/out/today.provider';

export class BrowserTodayProvider implements TodayProvider {
  constructor(private readonly now: () => Date = () => new Date()) {}

  today(): TodayOutcome {
    const now = this.now();

    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');

    const rawDate = `${year}-${month}-${day}`;

    return {
      kind: 'success',
      outcomeData: this.toOutcomeData(rawDate),
    };
  }

  private toOutcomeData(rawDate: string) {
    return { today: rawDate };
  }
}
