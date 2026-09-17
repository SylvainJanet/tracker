# Nutrition and activity

## Purpose

The historical tracker recorded calorie, protein and fibre intake, steps and
exercise as daily behavioural inputs. Tracking owns these actual observations
within a daily record. This application decision follows the cohesive daily
logging workflow rather than mechanically reproducing the spreadsheet layout.

Structured exercise definitions, prescriptions, plans and completed sessions
will belong to the future Training context. Physiological estimates remain
Analysis results rather than observations.

Calories are target- or range-oriented, while protein and fibre are principally
minimum-oriented. Activity values are observations; energy attributed to them is
derived. Daily variation is expected, so longer-window conclusions must remain
distinct from individual observations.

## Units

The tracker uses:

| Measurement                   | Unit                                         |
| ----------------------------- | -------------------------------------------- |
| Energy intake                 | kilocalories (`kcal`)                        |
| Protein                       | grams (`g`)                                  |
| Fibre                         | grams (`g`)                                  |
| Weight-adjusted protein rules | grams per kilogram (`g/kg`)                  |
| Energy-adjusted fibre rules   | grams per 1,000 kilocalories (`g/1000 kcal`) |
| Steps                         | count                                        |
| Exercise duration             | minutes                                      |
| Estimated activity energy     | kilocalories (`kcal`)                        |

These units are part of the meaning of the historical values and must not be
lost when the concepts are represented elsewhere.

## Nutrition observations

Historical records used calendar-day totals rather than individual meals.
Future meal planning may introduce foods or meal details, but calculated plan
totals must remain distinct from actual daily observations.

## Calorie intake

Calorie intake is the total recorded daily energy consumption in kilocalories.
Its interpretation depends on the effective goal, day type, applicable target
or range, measurement quality, completion and analysis window.

Historically, strict days used a central target, while big measured, regular
cheat and all-in cheat days could use ranges with their own measurement rules.
Falling outside a range does not retrospectively change the day type.

Calorie accuracy is independent of the presence of a number, completion and
day type. See
[Daily observations](daily-observations.md) for completion and measurement
quality.

## Nutrient goals and thresholds

Historically, protein used a weight-adjusted goal:

`protein goal = applicable weight × protein ratio`

Fibre used an energy-adjusted goal:

`fibre goal = applicable calorie reference / 1,000 × fibre ratio`

The applicable weight, calorie reference and rounding rules may depend on
the effective goals, rules and calculation method. The inputs and method used
for a date must remain explicit and historically traceable.

The workbook method used additive margins. Protein had minimum, low, goal and
high thresholds; fibre had low, goal and high thresholds, with an additional
minimum left open. The protein labels `Very low`, `Low`, `Under`, `Over` and
`High` described ascending threshold bands. Exact equality behaviour and
future labels remain calculation and presentation decisions.

The application need not reproduce separate spreadsheet helper balances for
every threshold and window. It must preserve which threshold was evaluated,
the applicable date-specific inputs and the resulting meaning. Because these
nutrients are minimum-oriented, values above the central goal are not
automatically failures.

### Protein and fibre measurement and rolling eligibility

In the redesigned workbook, protein and fibre adherence shared a
nutrition-measured indicator. A completed day with sufficiently accurate
nutrition recording participated in nutrient adherence calculations; an
intentionally unmeasured day did not contribute its nutrient intake, goals
or thresholds.

This rule did not define the treatment of calorie uncertainty, which followed
separate semantics for measured values, planned ranges and analytical
calculations.

Whether future accuracy belongs to the whole nutrition record or individual
values remains open.

Spreadsheet helper formulas sometimes represented an excluded day with numeric
zero when summing balances, but that zero was neither observed intake nor
evidence of compliance and must not become application state. Measured-day
counts and exclusions must remain available for explanation.

Historical cheat-day planning also demonstrated that intentionally unmeasured
total intake could coexist with separately known minimum nutritional intake.

Rolling evaluation uses each date’s effective goals, eligibility and threshold
inputs rather than applying the latest configuration to the whole window. See
[Analysis and boundaries](analysis-and-boundaries.md) for window calculations.

## Activity observations and derived energy

### Steps

Steps are a recorded daily count; absence means missing information, not
zero movement. The historical tracker distinguished total steps, a baseline
and excess steps that could contribute estimated energy.

Its conservative walking estimate was approximately:

`0.5 kcal × body weight in kilograms × distance in kilometres`

This formula is historical evidence, not an accepted application method.
The historical formula depended on the weight, distance conversion and steps
included; reproducing or comparing such results therefore requires those
assumptions to remain identifiable.

### Exercise

Completed exercise is an observation distinct from estimated energy
expenditure. The historical tracker recorded grouped durations. The performed
activity, estimation parameters and derived calories are distinct concepts and
should remain distinguishable.

Until Training is implemented, Tracking preserves the imported exercise label
and reported energy estimate with spreadsheet provenance. The stored energy
value remains an estimate rather than a measured physiological fact.

### Exercise planning

The workbook redesign developed a more concrete exercise-planning model:
reusable scheduled blocks, versioned prescriptions, day-specific planned
execution and all-or-nothing block completion. Moving a block to another day
was treated as a missed planned block plus an unplanned completion on the new
day.

These are historical design decisions and useful domain evidence for the future
Training context, but they do not by themselves determine its aggregates,
persistence model or final workflows.

### Overlap and maintenance

Device steps may include movement performed during exercise, creating possible
double counting between step- and exercise-derived energy. Any calculation
combining step- and exercise-derived energy needs an identifiable treatment of
this overlap, whether it accepts it, subtracts estimated steps or uses another
approach.

The historical tracker contained both maintenance estimates and separately
estimated activity expenditure, which creates a risk of counting the same
activity twice when combining them.

Later redesign work distinguished actual activity, planned activity and
activity deviation, and explored separating activity-inclusive maintenance
from an inferred baseline. That model was not completed in the spreadsheet
and should not be treated as implemented historical behaviour.

Maintenance and activity calculations remain exploratory, and an adopted
maintenance value represents a deliberate planning assumption rather than a
biological constant.

## Relationship with energy balance

Nutrition and activity may contribute to calorie-target adherence, estimated
physiological balance and comparison with adopted maintenance. These are
distinct results and must not be collapsed into one ambiguously named value.
Detailed formulas belong in [Analysis and boundaries](analysis-and-boundaries.md) and
[Calculation history](calculation-history.md).

## Durable semantics inferred from the historical tracker

- Units remain explicit.
- Nutrition and activity observations remain distinct from derived values.
- Missing or intentionally unmeasured information is not numeric zero.
- Completion and measurement quality remain independent.
- Excluded days do not become silently compliant.
- Nutrient thresholds retain their date-specific inputs and meaning.
- Protein and fibre above-goal values are not automatically failures.
- Rules are resolved per date before rolling aggregation.
- Step and exercise energy methods expose assumptions and possible overlap.
- Activity already represented in maintenance is not counted twice silently.
- Personal targets, foods and exercise schedules remain application data.

## Open domain questions

The following matters remain deliberately undecided:

- whether accuracy applies to the whole nutrition record or each value;
- which calorie reference determines the fibre goal for each day type;
- the precise equality behaviour at nutrition thresholds;
- whether fibre requires an additional minimum threshold;
- how nutrition statuses should be presented for unmeasured days;
- whether unmeasured days need a persisted reason;
- how step distance is calculated;
- whether step-derived calories use total or only excess steps in every
  mode;
- how overlap between exercise and steps is handled;
- how exercise calorie methods are versioned;
- whether activity adjustments affect adherence, physiological estimates
  or both;
- the validation ranges and precision for each observation.

These questions should be resolved through concrete nutrition, activity and
analysis use cases.
