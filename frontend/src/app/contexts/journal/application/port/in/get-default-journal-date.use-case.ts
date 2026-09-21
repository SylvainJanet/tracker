export interface GetDefaultJournalDateResultData {
  readonly defaultJournalDate: string;
}

export interface GetDefaultJournalDateResult {
  readonly kind: 'success';
  readonly resultData: GetDefaultJournalDateResultData;
}

export interface GetDefaultJournalDateUseCase {
  get(): GetDefaultJournalDateResult;
}
