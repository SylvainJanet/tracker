export type CompletionStatus = 'IN_PROGRESS' | 'COMPLETED';

export function isCompletionStatus(value: unknown): value is CompletionStatus {
  return value === 'IN_PROGRESS' || value === 'COMPLETED';
}
