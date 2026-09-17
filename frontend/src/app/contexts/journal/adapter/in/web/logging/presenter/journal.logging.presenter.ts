import { InjectionToken, type Signal, signal, type WritableSignal } from '@angular/core';

import { type CreateDailyRecordUseCase } from '../../../../../application/port/in/create-daily-record.use-case';
import {
  JournalLoggingModel,
  type DailyRecordLoggingReadModel,
} from '../model/journal.logging.model';
import type { GetDefaultDailyRecordDateUseCase } from '../../../../../application/port/in/get-default-daily-record-date.use-case';

export class JournalLoggingPresenter {
  private readonly currentModel: WritableSignal<JournalLoggingModel>;
  readonly model: Signal<DailyRecordLoggingReadModel>;

  constructor(
    private readonly createDailyRecord: CreateDailyRecordUseCase,
    private readonly getDefaultDailyRecordDate: GetDefaultDailyRecordDateUseCase,
  ) {
    const defaultDate = this.getDefaultDailyRecordDate.execute().resultData;
    this.currentModel = signal(JournalLoggingModel.initial(defaultDate));
    this.model = this.currentModel.asReadonly();
    this.currentModel.update((model) => model.selectWeight(1234));
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
        this.currentModel.update((model) => model.recordAlreadyExists(command.date));
        return;
      }

      this.currentModel.update((model) =>
        model.creationSucceeded({
          date: result.resultData.date,
          weight: result.resultData.weight,
        }),
      );
    } catch {
      this.currentModel.update((model) => model.creationFailed());
    }
  }

  selectDate(intent: string): void {
    this.currentModel.update((model) => model.selectDate(intent));
  }
}

export type JournalLoggingPresenterFactory = () => JournalLoggingPresenter;

export const JOURNAL_LOGGING_PRESENTER_FACTORY = new InjectionToken<JournalLoggingPresenterFactory>(
  'JournalLoggingPresenterFactory',
);
