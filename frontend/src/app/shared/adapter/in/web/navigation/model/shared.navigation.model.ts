export interface ContextDestination {
  readonly path: string;
  readonly label: string;
  readonly preserveQueryParameters: boolean;
}

export abstract class ContextNavigationModel {
  abstract readonly destinations: readonly ContextDestination[];
}
