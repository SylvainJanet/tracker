const resourceUrl = '/api/daily-records';

export class DailyRecordUrl {
  private constructor() {
    /* empty */
  }

  static createUrl(): string {
    return resourceUrl;
  }
}

export interface DailyRecordRequestBodyDto {
  readonly date: string;
  readonly weight: number;
}

export interface DailyRecordResponseDto {
  readonly date: string;
  readonly weight: number;
}
