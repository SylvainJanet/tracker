# Goals and day types

## Purpose

Goals describe the intended strategy for a period of time.

The strategy in force for a period may determine or contribute to:

- expected weight progression;
- strict-day calorie intake;
- protein and fibre goals;
- repeating day-type strategies and scheduled counts;
- permitted ranges for exceptional days;
- maintenance assumptions;
- weight-boundary parameters.

Goals and related rules change over time. Calculations for a date must use the
rules that applied on that date rather than the latest available configuration.

Strategy owns phases, goals, day-type definitions, schedules, per-date
assignments and other effective-dated policies. Analysis consumes resolved
Strategy representations when calculating expected trajectories, thresholds
and results.

The historical material distinguishes broad phases, effective goal sets and
day-type rules, and the workbook redesign introduced explicit review history.
These concepts represent different decisions, but their final model remains open.

## Phases

A phase is a meaningful period in the overall strategy.

Historical phases represented broad changes such as:

- an initial weight-loss period;
- later weight-loss periods with different calorie and scheduling strategies;
- transition into maintenance;
- later maintenance periods.

A phase may provide default boundaries for goals and rules, but phase and goal
are not interchangeable.

A phase answers:

> Which broad part of the strategy contains this date?

A goal answers:

> Which nutritional targets, expected-weight trajectory and repeating strategy
> apply to this date?

Other effective rules, such as calorie ranges, boundaries and adopted maintenance,
may change independently and are not necessarily part of the goal itself.

Phase identifiers should remain stable historical references. Changing the name
or parameters of the current strategy must not change the identity of older
phases.

## Goal and rule history

The workbook redesign associated a goal with identifiers, a phase, optional
start and end days, a mode, expected-weight endpoints, nutrition targets, a
cycle window, scheduled day-type counts and rounding parameters. This is
historical evidence, not an accepted aggregate or persistence schema.

For an evaluated date, the applicable decisions must be identifiable without
unintended gaps, overlaps or silent edits to older rules. A changed strategy
is normally represented by a new effective decision rather than mutation of
the previous one. Corrections to erroneous configuration remain a separate,
open problem.

The redesign also recorded reviews that captured why a deliberate review
occurred and the resulting set of selected goal and rule identifiers. Calculations
did not depend on review identifiers; the review history served as a record of
decisions. Whether the application requires the same review lifecycle, audit
model or grouping of independently changing rules remains undecided.

## Goal modes

The principal historical goal modes were:

- weight loss;
- maintenance.

The later workbook redesign also anticipated a temporary weight-gain goal during
maintenance when deliberate upward correction was required, without necessarily
creating a new phase.

### Weight-loss mode

Weight-loss mode defines an expected downward weight trajectory between the goal’s
expected starting and ending weights.

Daily observations are not required to follow that trajectory exactly. Evaluation
uses rolling information and boundaries.

### Maintenance mode

Maintenance mode evaluates whether weight remains within acceptable longer-term
boundaries.

Maintenance does not imply:

- identical weight every day;
- exact daily calorie balance;
- no temporary upward or downward movement.

The user prefers slight loss over gradual gain during maintenance, but the precise
expression of that preference belongs to the applicable maintenance and boundary
rules.

A difference between phase mode and goal mode must always be deliberate. The
tracker only anticipated such divergence around transitions into or within
maintenance.

## Repeating strategy

Later workbook phases used one-, two- and four-week repeating strategies. Their
scheduled day-type counts represented intended patterns rather than immutable
dates. The mature four-week pattern broadly combined strict days, a big measured
day in alternating weeks, a regular cheat opportunity and an all-in cheat
opportunity.

Historical placement choices and parameters are private configuration data.

## Day types

The tracker used four day types: `Strict`, `Big measured`, `Regular cheat` and
`All-in cheat`. A type contributes to the applicable intake rules and
measurement expectations; it is not inferred retrospectively from observed
calories.

### Strict day

A strict day follows the baseline calorie rule. The name describes intention,
not proof that targets were met or observations completed. An overage remains
an overage on a strict day unless the type is explicitly changed.

### Big measured day

A big measured day permits a higher calorie range while requiring accurate
recording. A large observed intake must not be reclassified automatically as
this type merely because it falls within that range.

### Regular cheat day

A regular cheat day is a planned higher-flexibility event with its own
applicable calorie rules. Historically, its permitted range was lower than
the corresponding all-in cheat range. Historical practice included accurately
measured home intake and intentionally unmeasured restaurant intake. The
enduring distinction is whether calorie intake was accurately measured, rather
than where the meal occurred.

### All-in cheat day

An all-in cheat day is an exceptional high-flexibility event, distinct from an
accidental overage, regular cheat day, big measured day or incomplete record.
Historical practice retained a minimum high-protein and high-fibre meal even
when other intake was unmeasured.

## Planning and rule resolution

The spreadsheet stored one daily type, but future use cases may distinguish
a planned type, a type resolved from the effective strategy and choices, and
the historical classification retained after the date. These distinctions
should be introduced only with concrete editing and transition rules.

A repeating strategy provides default expectations and scheduled counts. A
future user may need to choose whether an exceptional event will be accurately
measured, move an exceptional day, replace a planned cheat day or record an
unscheduled event. A per-date choice must not mutate the underlying goal.

Measurement requirements are separate from day type. Strict and Big measured
days historically required accurate calorie recording, while Regular cheat
and All-in cheat days could be accurately measured or intentionally unmeasured.
In the final workbook redesign, whether a scheduled cheat opportunity required
measurement or allowed it to be omitted was part of the goal’s scheduled
counts, not a separate actual day type.

Resolution must identify the effective strategy and independently applicable
rules for the date. Where repeating strategies matter, the goal-period start,
cycle window and scheduled counts provide relevant context, but the exact
treatment of cycle positions, moved events and unused opportunities remains
unresolved.

## Relationships

Goals and day types describe intention and applicable rules; daily
observations describe what happened. A strict day can exceed its target,
a planned exceptional day can remain incomplete, and a permitted exceptional
day can be complete but unmeasured. Neither plans nor observations may
rewrite or fabricate the other.

Tracking may reference the stable Strategy classification applied to a daily
record, but Strategy remains responsible for its definition, resolution and
history.

Nutrition analysis combines effective targets and measurement requirements
with actual observations and their quality. See
[Nutrition and activity](nutrition-and-activity.md) and
[Analysis and boundaries](analysis-and-boundaries.md).

Goal history may define expected-weight endpoints, while separately effective
maintenance and boundary rules provide other references used in weight
evaluation. A change may alter expectations and boundaries, but not measured
or predicted weights. See [Weight and projections](weight-and-projections.md).

## Established domain constraints

- Phases, goals and rules retain stable historical identities.
- Changes are effective-dated rather than silently retroactive.
- Applicable timelines avoid unintended gaps and overlaps.
- Phase and goal remain distinct concepts.
- Day type is not inferred from observed intake.
- Strict, big measured, regular cheat and all-in cheat remain distinct.
- Measurement requirements are explicit and independent of completion.
- An unmeasured option does not produce invented intake.
- A per-date choice does not mutate the underlying strategy.
- Private targets and parameters remain application data.

## Questions not resolved by the historical tracker

The following matters remain deliberately undecided:

- how cycle positions are represented;
- whether repeating strategies produce stored plans or derived plans
- how far in advance day-type choices may be made;
- whether planned, resolved and historical day-type classifications
  require separate application concepts;
- how overrides affect later positions in a repeating cycle;
- whether unused cheat opportunities can move or accumulate;
- how corrections to erroneous historical rules differ from new
  decisions;
- whether all rule histories share one effective-period abstraction.

These questions should be resolved through concrete planning, review and
rule-resolution use cases.
