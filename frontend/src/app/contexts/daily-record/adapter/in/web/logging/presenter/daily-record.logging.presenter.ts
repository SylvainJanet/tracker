import { InjectionToken, type Signal, signal, type WritableSignal } from '@angular/core';

import { type CreateDailyRecordUseCase } from '../../../../../application/port/in/create-daily-record.use-case';
import {
  DailyRecordLoggingModel,
  type DailyRecordLoggingReadModel,
} from '../model/daily-record.logging.model';
import { DailyRecordLoggingMapper } from './mapper/daily-record.logging.mapper';
import type { GetDefaultDailyRecordDateUseCase } from '../../../../../application/port/in/get-default-daily-record-date.use-case';

export class DailyRecordLoggingPresenter {
  private readonly currentModel: WritableSignal<DailyRecordLoggingModel>;
  readonly model: Signal<DailyRecordLoggingReadModel>;

  constructor(
    private readonly createDailyRecord: CreateDailyRecordUseCase,
    private readonly getDefaultDailyRecordDate: GetDefaultDailyRecordDateUseCase,
  ) {
    const defaultDate = this.getDefaultDailyRecordDate.execute().resultData;
    this.currentModel = signal(DailyRecordLoggingModel.initial(defaultDate));
    this.model = this.currentModel.asReadonly();
  }

  async create(): Promise<void> {
    const transition = this.currentModel().requestCreation();
    this.currentModel.set(transition.model);

    if (transition.effect.kind === 'none') {
      return;
    }

    try {
      const result = await this.createDailyRecord.execute(transition.effect.command);
      if (result.kind === 'already-exists') {
        const command = transition.effect.command;
        this.currentModel.update((model) => model.recordAlreadyExists(command));
        return;
      }

      this.currentModel.update((model) =>
        model.creationSucceeded(DailyRecordLoggingMapper.toView(result.resultData)),
      );
    } catch {
      this.currentModel.update((model) => model.creationFailed());
    }
  }

  selectDate(intent: string): void {
    this.currentModel.update((model) => model.selectDate(intent));
  }
}

export type DailyRecordLoggingPresenterFactory = () => DailyRecordLoggingPresenter;

export const DAILY_RECORD_LOGGING_PRESENTER_FACTORY =
  new InjectionToken<DailyRecordLoggingPresenterFactory>('DailyRecordLoggingPresenterFactory');
