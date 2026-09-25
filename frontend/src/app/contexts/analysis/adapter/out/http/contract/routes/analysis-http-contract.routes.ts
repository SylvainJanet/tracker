export class AnalysisHttpContractRoutes {
  private constructor() {
    /* empty */
  }

  private static readonly RESOURCE_URL = '/api/analysis';

  static getWeightAnalysisUrl(): string {
    return AnalysisHttpContractRoutes.RESOURCE_URL + '/weight';
  }
}
