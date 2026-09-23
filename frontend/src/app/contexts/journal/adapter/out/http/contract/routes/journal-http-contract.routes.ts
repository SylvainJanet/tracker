export class JournalHttpContractRoutes {
  private constructor() {
    /* empty */
  }

  private static readonly RESSOURCE_URL = '/api/journal';

  static logWeightMeasurementUrl(): string {
    return JournalHttpContractRoutes.RESSOURCE_URL + '/weight-measurement';
  }

  static getWeightMeasurementByDateUrl(date: string): string {
    return JournalHttpContractRoutes.RESSOURCE_URL + `/weight-measurement/${date}`;
  }
}
