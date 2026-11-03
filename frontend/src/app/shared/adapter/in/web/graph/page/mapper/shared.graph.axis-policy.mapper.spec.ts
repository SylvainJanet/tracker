import { describe, expect, it } from 'vitest';

import { SharedGraphAxisPolicyMapper } from './shared.graph.axis-policy.mapper';

describe('SharedGraphAxisPolicyMapper', () => {
  it('preserves a discrete extent and labels it from its minimum at the configured interval', () => {
    expect(
      SharedGraphAxisPolicyMapper.scaleFor(
        {
          minimum: 3,
          maximum: 30,
        },
        {
          kind: 'discrete',
          intervals: [
            { maximumSpanExclusive: 14, interval: 1 },
            { maximumSpanExclusive: 84, interval: 7 },
            { maximumSpanExclusive: 168, interval: 14 },
          ],
          fallbackTargetLabelCount: 9,
        },
      ),
    ).toEqual({
      minimum: 3,
      maximum: 30,
      interval: 7,
      labelValues: [3, 10, 17, 24],
    });
  });
  it('rounds and pads a continuous extent while producing stable decimal labels', () => {
    expect(
      SharedGraphAxisPolicyMapper.scaleFor(
        {
          minimum: 81.9,
          maximum: 82,
        },
        {
          kind: 'continuous',
          intervals: [
            { maximumSpanExclusive: 0.2, interval: 0.05 },
            { maximumSpanExclusive: 2, interval: 0.1 },
            { maximumSpanExclusive: 5, interval: 0.5 },
            { maximumSpanExclusive: 20, interval: 1 },
          ],
          fallbackTargetLabelCount: 8,
        },
      ),
    ).toEqual({
      minimum: 81.85,
      maximum: 82.05,
      interval: 0.05,
      labelValues: [81.85, 81.9, 81.95, 82, 82.05],
    });
  });

  it('expands a flat continuous extent to provide enough labels', () => {
    expect(
      SharedGraphAxisPolicyMapper.scaleFor(
        {
          minimum: 82.1,
          maximum: 82.1,
        },
        {
          kind: 'continuous',
          intervals: [
            { maximumSpanExclusive: 0.2, interval: 0.05 },
            { maximumSpanExclusive: 2, interval: 0.1 },
            { maximumSpanExclusive: 5, interval: 0.5 },
            { maximumSpanExclusive: 20, interval: 1 },
          ],
          fallbackTargetLabelCount: 8,
        },
      ),
    ).toEqual({
      minimum: 82,
      maximum: 82.2,
      interval: 0.05,
      labelValues: [82, 82.05, 82.1, 82.15, 82.2],
    });
  });
  it('chooses a nice fallback interval for a long discrete span', () => {
    expect(
      SharedGraphAxisPolicyMapper.scaleFor(
        {
          minimum: 3,
          maximum: 403,
        },
        {
          kind: 'discrete',
          intervals: [
            { maximumSpanExclusive: 14, interval: 1 },
            { maximumSpanExclusive: 84, interval: 7 },
            { maximumSpanExclusive: 168, interval: 14 },
          ],
          fallbackTargetLabelCount: 9,
        },
      ),
    ).toEqual({
      minimum: 3,
      maximum: 403,
      interval: 50,
      labelValues: [3, 53, 103, 153, 203, 253, 303, 353, 403],
    });
  });

  it('uses an explicit interval override instead of the automatic interval', () => {
    expect(
      SharedGraphAxisPolicyMapper.scaleFor(
        {
          minimum: 3,
          maximum: 30,
        },
        {
          kind: 'discrete',
          intervals: [
            { maximumSpanExclusive: 14, interval: 1 },
            { maximumSpanExclusive: 84, interval: 7 },
            { maximumSpanExclusive: 168, interval: 14 },
          ],
          fallbackTargetLabelCount: 9,
        },
        14,
      ),
    ).toEqual({
      minimum: 3,
      maximum: 30,
      interval: 14,
      labelValues: [3, 17],
    });
  });
  it('treats a decimal span mathematically equal to a threshold as being on that threshold', () => {
    expect(
      SharedGraphAxisPolicyMapper.scaleFor(
        {
          minimum: 62.1,
          maximum: 82.1,
        },
        {
          kind: 'continuous',
          intervals: [
            { maximumSpanExclusive: 0.2, interval: 0.05 },
            { maximumSpanExclusive: 2, interval: 0.1 },
            { maximumSpanExclusive: 5, interval: 0.5 },
            { maximumSpanExclusive: 20, interval: 1 },
          ],
          fallbackTargetLabelCount: 8,
        },
      ),
    ).toEqual({
      minimum: 60,
      maximum: 85,
      interval: 5,
      labelValues: [60, 65, 70, 75, 80, 85],
    });
  });

  it('keeps a fallback interval close to the target label count', () => {
    expect(
      SharedGraphAxisPolicyMapper.scaleFor(
        {
          minimum: 3,
          maximum: 171,
        },
        {
          kind: 'discrete',
          intervals: [
            { maximumSpanExclusive: 14, interval: 1 },
            { maximumSpanExclusive: 84, interval: 7 },
            { maximumSpanExclusive: 168, interval: 14 },
          ],
          fallbackTargetLabelCount: 9,
        },
      ),
    ).toEqual({
      minimum: 3,
      maximum: 171,
      interval: 20,
      labelValues: [3, 23, 43, 63, 83, 103, 123, 143, 163],
    });
  });
});
