export interface TodayOutcomeData {
  readonly today: string;
}

export interface TodayOutcome {
  readonly kind: 'success';
  readonly outcomeData: TodayOutcomeData;
}

export interface TodayProvider {
  today(): TodayOutcome;
}
