import { InjectionToken, type Signal, signal, type WritableSignal } from '@angular/core';

import { type GetDailyRecordUseCase } from '../../../../../application/port/in/get-daily-record.use-case';
import {
  DailyRecordSummaryModel,
  type DailyRecordSummaryReadModel,
} from '../model/daily-record.summary.model';
import { DailyRecordSummaryMapper } from './mapper/daily-record.summary.mapper';
import type { GetDefaultDailyRecordDateUseCase } from '../../../../../application/port/in/get-default-daily-record-date.use-case';

export class DailyRecordSummaryPresenter {
  private readonly currentModel: WritableSignal<DailyRecordSummaryModel>;
  readonly model: Signal<DailyRecordSummaryReadModel>;

  constructor(
    private readonly getDailyRecord: GetDailyRecordUseCase,
    private readonly getDefaultDailyRecordDate: GetDefaultDailyRecordDateUseCase,
  ) {
    const defaultDate = this.getDefaultDailyRecordDate.execute().resultData;
    this.currentModel = signal(DailyRecordSummaryModel.initial(defaultDate));
    this.model = this.currentModel.asReadonly();
  }

  async selectDate(intent: string): Promise<void> {
    const transition = this.currentModel().selectDate(intent);

    this.currentModel.set(transition.model);

    if (transition.effect.kind === 'none') {
      return;
    }

    try {
      const result = await this.getDailyRecord.execute(transition.effect.query);

      if (result.kind === 'not-found') {
        const query = transition.effect.query;
        this.currentModel.update((model) => model.recordNotFound(query));
        return;
      }

      this.currentModel.update((model) =>
        model.recordFound(DailyRecordSummaryMapper.toView(result.resultData)),
      );
    } catch {
      this.currentModel.update((model) => model.recordLoadingFailed());
    }
  }
}

export type DailyRecordSummaryPresenterFactory = () => DailyRecordSummaryPresenter;

export const DAILY_RECORD_SUMMARY_PRESENTER_FACTORY =
  new InjectionToken<DailyRecordSummaryPresenterFactory>('DailyRecordSummaryPresenterFactory');
