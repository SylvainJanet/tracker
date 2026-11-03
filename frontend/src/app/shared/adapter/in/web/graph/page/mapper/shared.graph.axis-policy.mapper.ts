import type {
  SharedGraphAxisExtentView,
  SharedGraphAxisPolicyView,
  SharedGraphAxisScaleView,
} from '../../model/view/shared.graph.model.view';

export class SharedGraphAxisPolicyMapper {
  private constructor() {
    /* empty */
  }

  static scaleFor(
    extent: SharedGraphAxisExtentView,
    policy: SharedGraphAxisPolicyView,
    intervalOverride?: number,
  ): SharedGraphAxisScaleView {
    const span = SharedGraphAxisPolicyMapper.normalize(extent.maximum - extent.minimum);
    const configuredInterval = policy.intervals.find(
      ({ maximumSpanExclusive }) => span < maximumSpanExclusive,
    )?.interval;
    const interval =
      intervalOverride ??
      configuredInterval ??
      SharedGraphAxisPolicyMapper.fallbackIntervalFor(extent, policy);
    const resolvedExtent =
      policy.kind === 'continuous'
        ? SharedGraphAxisPolicyMapper.continuousExtentFor(extent, interval)
        : extent;
    const resolvedSpan = resolvedExtent.maximum - resolvedExtent.minimum;
    const labelCount = Math.floor(resolvedSpan / interval + 1e-9) + 1;

    return {
      minimum: resolvedExtent.minimum,
      maximum: resolvedExtent.maximum,
      interval,
      labelValues: Array.from({ length: labelCount }, (_, index) =>
        SharedGraphAxisPolicyMapper.normalize(resolvedExtent.minimum + index * interval),
      ),
    };
  }

  private static continuousExtentFor(
    extent: SharedGraphAxisExtentView,
    interval: number,
  ): SharedGraphAxisExtentView {
    const roundedMinimum = SharedGraphAxisPolicyMapper.normalize(
      Math.floor(SharedGraphAxisPolicyMapper.snappedQuotient(extent.minimum, interval)) * interval,
    );
    const roundedMaximum = SharedGraphAxisPolicyMapper.normalize(
      Math.ceil(SharedGraphAxisPolicyMapper.snappedQuotient(extent.maximum, interval)) * interval,
    );
    let minimum = SharedGraphAxisPolicyMapper.sameValue(roundedMinimum, extent.minimum)
      ? SharedGraphAxisPolicyMapper.normalize(roundedMinimum - interval)
      : roundedMinimum;
    let maximum = SharedGraphAxisPolicyMapper.sameValue(roundedMaximum, extent.maximum)
      ? SharedGraphAxisPolicyMapper.normalize(roundedMaximum + interval)
      : roundedMaximum;
    let intervalCount = Math.round((maximum - minimum) / interval);

    while (intervalCount < 4) {
      const lowerPadding = extent.minimum - minimum;
      const upperPadding = maximum - extent.maximum;

      if (lowerPadding <= upperPadding) {
        minimum = SharedGraphAxisPolicyMapper.normalize(minimum - interval);
      } else {
        maximum = SharedGraphAxisPolicyMapper.normalize(maximum + interval);
      }

      intervalCount += 1;
    }

    return {
      minimum,
      maximum,
    };
  }

  private static snappedQuotient(value: number, interval: number): number {
    const quotient = value / interval;
    const nearestInteger = Math.round(quotient);
    const tolerance = Number.EPSILON * Math.max(1, Math.abs(quotient)) * 8;

    return Math.abs(quotient - nearestInteger) <= tolerance ? nearestInteger : quotient;
  }

  private static sameValue(first: number, second: number): boolean {
    const tolerance = Number.EPSILON * Math.max(1, Math.abs(first), Math.abs(second)) * 8;

    return Math.abs(first - second) <= tolerance;
  }

  private static normalize(value: number): number {
    return Number(value.toPrecision(12));
  }

  private static fallbackIntervalFor(
    extent: SharedGraphAxisExtentView,
    policy: SharedGraphAxisPolicyView,
  ): number {
    const span = SharedGraphAxisPolicyMapper.normalize(extent.maximum - extent.minimum);

    if (span === 0) {
      return 1;
    }

    const targetLabelCount = policy.fallbackTargetLabelCount;
    const targetInterval = span / Math.max(targetLabelCount - 1, 1);
    const magnitude = 10 ** Math.floor(Math.log10(targetInterval));
    const candidates = [
      ...new Set(
        [1, 2, 5, 10].map((multiplier) => {
          const interval = multiplier * magnitude;

          return policy.kind === 'discrete' ? Math.max(1, interval) : interval;
        }),
      ),
    ];
    let selectedInterval = candidates[0] ?? 1;
    let selectedLabelCount = SharedGraphAxisPolicyMapper.labelCountFor(
      extent,
      policy.kind,
      selectedInterval,
    );

    for (const candidate of candidates.slice(1)) {
      const candidateLabelCount = SharedGraphAxisPolicyMapper.labelCountFor(
        extent,
        policy.kind,
        candidate,
      );
      const candidateCountDistance = Math.abs(candidateLabelCount - targetLabelCount);
      const selectedCountDistance = Math.abs(selectedLabelCount - targetLabelCount);

      if (
        candidateCountDistance < selectedCountDistance ||
        (candidateCountDistance === selectedCountDistance &&
          Math.abs(candidate - targetInterval) < Math.abs(selectedInterval - targetInterval))
      ) {
        selectedInterval = candidate;
        selectedLabelCount = candidateLabelCount;
      }
    }

    return selectedInterval;
  }

  private static labelCountFor(
    extent: SharedGraphAxisExtentView,
    kind: SharedGraphAxisPolicyView['kind'],
    interval: number,
  ): number {
    const resolvedExtent =
      kind === 'continuous'
        ? SharedGraphAxisPolicyMapper.continuousExtentFor(extent, interval)
        : extent;
    const resolvedSpan = resolvedExtent.maximum - resolvedExtent.minimum;

    return Math.floor(resolvedSpan / interval + 1e-9) + 1;
  }
}
