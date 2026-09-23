import { type JournalLoggingModel } from '../../journal.logging.model';
import type { TransitionEffect } from '../../../../../../../../../shared/api/shared.component.model';

export const LogTransitionEffectKind = ['log'] as const;
export const GetTransitionEffectKind = ['get'] as const;

interface LogTransitionEffectPayloads {
  log: undefined;
}

interface GetTransitionEffectPayloads {
  get: undefined;
}

export type LogTransitionEffect = TransitionEffect<
  (typeof LogTransitionEffectKind)[number],
  LogTransitionEffectPayloads,
  JournalLoggingModel
>;

export type GetTransitionEffect = TransitionEffect<
  (typeof GetTransitionEffectKind)[number],
  GetTransitionEffectPayloads,
  JournalLoggingModel
>;
