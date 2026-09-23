export type StateDimension<
  Kind extends string,
  Payloads extends Record<Kind, unknown> & Record<Exclude<keyof Payloads, Kind>, never>,
> = {
  [K in Kind]: {
    readonly kind: K;
  } & (Payloads[K] extends undefined ? unknown : Payloads[K]);
}[Kind];

export type TransitionEffect<
  Kind extends string,
  Payloads extends Record<Kind, unknown> & Record<Exclude<keyof Payloads, Kind>, never>,
  Model extends { readonly state: { [k in keyof Model['state']]: { readonly kind: string } } },
> =
  | ({
      readonly kind: 'accepted';
      readonly transition: Model;
    } & {
      readonly effect: {
        [K in Kind]: {
          readonly kind: K;
        } & (Payloads[K] extends undefined ? unknown : Payloads[K]);
      }[Kind];
    })
  | {
      readonly kind: 'ignored';
      readonly transition: Model;
    };

export type StateKinds<
  State extends {
    [K in keyof State]: StateDimension<string, Record<string, unknown> & Record<never, never>>;
  },
> = {
  [K in keyof State]: State[K]['kind'];
};

export function stateKindsOf<
  State extends {
    [K in keyof State]: StateDimension<string, Record<string, unknown> & Record<never, never>>;
  },
>(pageState: State): StateKinds<State> {
  return Object.fromEntries(
    Object.keys(pageState).map((key) => [key, pageState[key as keyof State]['kind']]),
  ) as StateKinds<State>;
}

export interface StateKindsTransition<
  State extends {
    [K in keyof State]: StateDimension<string, Record<string, unknown> & Record<never, never>>;
  },
> {
  readonly from: StateKinds<State>;
  readonly to: StateKinds<State>[];
}

export interface StateTransition<
  State extends {
    [K in keyof State]: StateDimension<string, Record<string, unknown> & Record<never, never>>;
  },
  Kind extends string = string,
> {
  kind: Kind;
  transitions: StateKindsTransition<State>[];
}
