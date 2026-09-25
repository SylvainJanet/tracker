# Weight and projections

## Purpose

Weight information serves several different purposes in the tracker:

- recording actual measurements;
- filling gaps between measurements;
- providing sensible estimated values for future dates;
- defining expected weight trajectories from goals;
- evaluating short and long-term deviations;
- supporting calculations whose parameters depend on weight.

These purposes require several kinds of values that must remain distinguishable.

Journal owns weight measurements as independent dated observations and owns
manually entered predictions as a separate aggregate. Strategy owns the goal
trajectory definition. Analysis owns expected, interpolated, carried and other
derived weights.

A weight associated with a date may be:

- measured;
- manually predicted;
- interpolated;
- expected from an applicable goal.

They are not interchangeable merely because they share the same unit and date.

## Predicted weight

A predicted weight is a manually entered estimate for a future date.

Predictions were introduced because carrying the last measured weight
indefinitely produced unrealistic future values, particularly during a
weight-loss phase.

They serve two purposes:

- providing a plausible value on the predicted date;
- acting as a future endpoint for interpolation.

A predicted weight is an input chosen by the user. It is not the same as an
expected weight calculated from a goal trajectory.

## Present-day treatment

In the spreadsheet, a value already entered for the present day was treated
as sufficiently current for the day’s calculations, even when it originated
as a future prediction.

The user could later replace it manually after taking an actual measurement.

This behaviour was convenient in the spreadsheet, but it did not preserve the
distinction between the value's original predicted status and its present-day
classification. How the application represents that distinction is an
application-domain question.

The spreadsheet's date-based treatment was an implementation convention, not a
change in the meaning of the value. Reaching the current date does not by
itself turn a prediction into a measured observation. How a prediction is
confirmed, replaced or otherwise resolved when its date arrives remains a
separate application use-case decision.

## Expected weight

Expected weight is calculated from the goal linear trajectory applicable to a
date.

In the redesigned spreadsheet, expected weight was calculated as the trajectory
between the expected start and end weights of the applicable goal period.

Expected weight is a target or reference. It is not an observation and is not
intended to fill missing measurements.

## Weight resolution and provenance

In the spreadsheet, a calculation that needed weight first used the direct value
entered in `Daily`, when one existed. Otherwise it derived a value using linear
interpolation, carry forward or carry backward according to the available
endpoints. If no usable endpoint existed, the result remained blank.

`Weight source` and `Weight method` exposed the provenance of the effective value.
The effective weight was a calculation concept rather than another observation.

## Interpolation

For endpoints `d0` and `d1` with weights `w0` and `w1`, linear interpolation
for date `d` is:

`w(d) = w0 + (w1 - w0) × (d - d0) / (d1 - d0)`

Date differences are measured in calendar days. The spreadsheet distinguished:

- `Measured`: direct value dated today or earlier
- `Predicted`: direct future value
- `Interpolated`: derived value dated today or earlier
- `Interpolated (prediction)`: derived future value

It separately recorded how the value was produced using `Direct`, `Linear`,
`Carry forward`, or `Carry backward`.

A future prediction could serve as an interpolation endpoint in the
spreadsheet, but it remains an input rather than a measurement or an expected
weight. Changing or replacing it may recalculate affected derived values without
changing the meaning of the original inputs.

When no later endpoint existed, the spreadsheet could carry the last
measurement forward rather than inventing a trend. This fallback is not a
measurement for every intervening date. When no later direct weight existed,
the spreadsheet carried the previous direct weight forward. If only a later
direct weight existed, it carried that value backward. These fallbacks produced
derived values; they did not create measurements for the affected dates.

The spreadsheet made derived weight reproducible from the relevant endpoints
and exposed source and method information alongside the calculated value.

## Goals and analysis

Goal history determines expected weight trajectories. Changing a goal may alter
expected weight, boundaries and progress results, but it must not change
measurements, historical predictions or their provenance.

Boundary calculations consume resolved and reference weights, analysis windows,
parameters and the applicable method; they are not part of measurement or
interpolation. See [Analysis and boundaries](analysis-and-boundaries.md).

## Rounding

Rounding can affect trajectories, weight-dependent goals and boundaries.

The spreadsheet decision was fairly concrete:

- intermediate calculations should retain full numerical precision;
- display formatting controls visible precision;
- `ROUND()` is used only when rounding is deliberately part of a calculation
  rule.

## Corrections and replacement

Correcting a direct weight or replacing a previously entered future value changed
the input in `Daily`, after which dependent calculations recalculated. The
spreadsheet did not establish a separate audit history for superseded values.

Whether corrected measurements and superseded predictions require a complete
audit history remains open.

## Domain constraints

- Every weight value has a calendar date and unit.
- Measured, predicted, interpolated and expected values remain distinguishable.
- Missing weight is not numeric zero.
- Goals and boundaries must not modify observations or historical inputs.
- Direct entered weights took precedence over derived weights.
- Future predicted weights could act as interpolation endpoints.
- Missing weights were resolved through linear interpolation, carry forward,
  carry backward, or blank according to endpoint availability.

## Open domain questions

Revision history, prediction resolution and historical as-of replay remain open
application-domain questions.
