import { InjectionToken, type Signal, signal, type WritableSignal } from '@angular/core';

import {
  JournalSummaryModel,
  type DailyRecordSummaryReadModel,
} from '../model/journal.summary.model';
import type { GetDefaultDailyRecordDateUseCase } from '../../../../../application/port/in/get-default-daily-record-date.use-case';

export class JournalSummaryPresenter {
  private readonly currentModel: WritableSignal<JournalSummaryModel>;
  readonly model: Signal<DailyRecordSummaryReadModel>;

  constructor(private readonly getDefaultDailyRecordDate: GetDefaultDailyRecordDateUseCase) {
    const defaultDate = this.getDefaultDailyRecordDate.execute().resultData;
    this.currentModel = signal(JournalSummaryModel.initial(defaultDate));
    this.model = this.currentModel.asReadonly();
  }

  async selectDate(intent: string): Promise<void> {
    const transition = this.currentModel().selectDate(intent);
    this.currentModel.set(transition.model);

    if (transition.effect.kind === 'none') {
      return;
    }

    const view = { date: transition.effect.query };
    this.currentModel.update((model) => model.recordFound(view));
  }
}

export type JournalSummaryPresenterFactory = () => JournalSummaryPresenter;

export const JOURNAL_SUMMARY_PRESENTER_FACTORY = new InjectionToken<JournalSummaryPresenterFactory>(
  'JournalSummaryPresenterFactory',
);
