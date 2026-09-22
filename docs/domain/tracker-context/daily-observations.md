# Daily observations

## Purpose

Daily observations represent information recorded about what actually happened on
a calendar date.

The historical tracker stored daily totals rather than individual meals. The user
generally follows one meal a day, so a cheat day is normally also a cheat meal.
Future meal-planning features may introduce more detailed concepts, but they must
not change the meaning of existing daily totals.

A daily view may eventually combine observations with plans, goals, predictions
and calculated results. That does not mean all information displayed for a date
belongs to the same aggregate.

## Historical daily information

The redesigned tracker’s `01 Daily` sheet contained:

| Field             | Meaning                                             |
| ----------------- | --------------------------------------------------- |
| Day               | Sequential day within the tracking history          |
| Date              | Calendar date of the observation                    |
| Complete          | Whether logging for the day was declared complete   |
| Weight (kg)       | Weight entered for the date, when available         |
| Calories (kcal)   | Calorie value entered for the date                  |
| Protein (g)       | Protein value entered for the date                  |
| Fibre (g)         | Fibre value entered for the date                    |
| Day type          | Strict, Big measured, Regular cheat or All-in cheat |
| Calories accurate | Whether calorie intake was sufficiently accurate    |
| Steps             | Step count entered for the date                     |
| Exercise          | Exercise performed on the date                      |

This spreadsheet grouping is historical. The application may model some of these
concepts independently when they have different lifecycles or consistency rules.

The spreadsheet also pre-populated future rows with planned calorie, protein,
fibre, day-type and activity values. Those values were planning conveniences
stored in the same table, not observations of actual events. The application does
not need to preserve that physical grouping.

## Date and day number

A daily observation belongs to one civil calendar date.

The spreadsheet’s sequential Day value is a derived position relative to the
beginning of the tracking history and may still be useful for phase resolution,
repeating schedules or presentation.

## Progressive recording

Information is commonly entered throughout the day.

Completion was recorded explicitly rather than inferred from the date or from
the presence of individual observations. A day could therefore contain information
while logging was still incomplete.

## Completion and measurement accuracy

In the historical spreadsheet, completion and calorie accuracy were separate
concerns.

A completed day could be intentionally unmeasured because completion meant that
logging decisions for the day were finished; it did not guarantee that calorie
intake was measured accurately. Likewise, an incomplete day could already
contain an accurate calorie value while other information remained to be
entered.

These historical semantics do not establish a completion concept for the
current Journal. If a later use case introduces one, its relationship with
measurement accuracy must be decided explicitly rather than inferred from the
spreadsheet columns.

## Observed values

### Nutrition

On accurately measured days, calories represent total recorded daily energy
intake in kilocalories, while protein and fibre represent total daily intake
in grams.

A numeric value and its measurement quality are separate information; presence
alone does not make a value accurate enough for every calculation. Historical
spreadsheet cells on intentionally unmeasured cheat days may nevertheless
contain planning values, ceilings or other placeholders. Such numeric presence
must not cause an import to classify them as measured observations.

The spreadsheet recorded calorie accuracy explicitly. For adherence calculations,
a completed accurately measured day also served as the shared indication that its
protein and fibre observations participated. Protein and fibre were expected to
be entered on such days.

In the redesigned tracker, intentionally unmeasured cheat days were excluded
from protein and fibre adherence.
Unmeasured days were not assigned estimated intake retrospectively.

Detailed nutrition semantics belong in
[Nutrition and activity](nutrition-and-activity.md).

### Activity

Steps are a recorded count for the date. Exercise is intentionally performed
physical activity beyond ordinary movement. Missing steps do not mean zero, and
step or exercise-derived energy is a calculation rather than an observation.

### Weight

The spreadsheet weight entry represented an observation, distinct from
predictions and calculated weights. Its placement in a daily row does not
establish a daily-record aggregate; see
[Weight and projections](weight-and-projections.md).

## Missing, zero and intentionally unmeasured

The [shared terminology](README.md#shared-terminology) defines these states. For
daily observations:

- **Missing** means no value was provided and no decision declares it
  unmeasured.
- **Zero** is an explicitly recorded numeric observation.
- **Intentionally unmeasured** means logging is finished without a
  sufficiently accurate value for an applicable measurement.

An unmeasured value is neither zero nor silently compliant. It may be
excluded from an applicable calculation, but its exclusion must remain visible
and understandable.

## Day type and observation quality

Day type and observation quality are distinct concepts. Day type does not
determine completion or whether every observation has been entered, but some
historical day types imposed calorie-measurement requirements. Strict and Big
measured days required accurately measured calories, while Regular and All-in
cheat days could be measured or intentionally unmeasured.

Calculations must consider both the applicable rule and observation quality.

The day type must not be inferred from intake: exceeding a strict target does
not turn the day into a cheat day. Detailed categories and scheduling belong
in [Goals and day types](goals-and-day-types.md).

## Corrections

Historical observations may need correction when an entry mistake is discovered.

Correcting an observation is different from changing the goal or calculation
method that applied, entering a later prediction, or retrospectively
reclassifying what occurred.

The precise correction and audit policy remains undecided. If a later use case
introduces completion, that state must not implicitly make observations
permanently uneditable or permit unrestricted history rewriting.

## Open domain questions

The following matters remain deliberately undecided:

- whether later Journal use cases need an explicit day-completion concept;
- if completion is introduced, which observations are mandatory beforehand;
- if completion is introduced, whether correction requires reopening the day;
- whether measurement accuracy belongs to each nutrition value or to the day;
- whether an intentionally unmeasured day needs a reason;
- whether planned and actual day types must be stored separately;
- how future structured Training completions update or reference performed
  exercise observations;
- which numerical validations apply to each observation.

These questions should be resolved through concrete use cases rather than
speculative modelling.
