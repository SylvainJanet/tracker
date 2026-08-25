const resourceUrl = '/api/daily-records';

export class DailyRecordUrl {
  private constructor() {
    /* empty */
  }

  static findByDate(request: GetDailyRecordRequestParameters): string {
    return resourceUrl + '/' + request.date;
  }

  static createUrl(): string {
    return resourceUrl;
  }
}

export interface GetDailyRecordRequestParameters {
  readonly date: string;
}

export interface CreateDailyRecordRequestBodyDto {
  readonly date: string;
}

export interface DailyRecordResponseDto {
  readonly date: string;
  readonly status: string;
}
