# Analysis and boundaries

## Purpose

Analysis transforms observations, goals and calculation rules into derived
results about adherence, weight trends and sustained deviations. Results must
remain distinct from the observations, predictions, goals and historical
decisions used to produce them.

Analysis owns calculation-method history and these derived results. It consumes
explicit representations published by Journal and Strategy rather than their
internal domain objects or persistence models. Reusable numerical operations may
be delegated to Statistics without giving that supporting module the business
meaning of the inputs or results.

The historical tracker and its redesign used several rolling windows because
short- and long-lived changes have different meanings. Future application use
cases may retain, rename or replace those windows and methods.

## Historical windows and eligibility

The workbook used these fixed day-count windows:

| Historical identifier | Duration | Intended perspective                      |
| --------------------- | -------: | ----------------------------------------- |
| `1W`                  |   7 days | Recent behaviour                          |
| `2W`                  |  14 days | Short-cycle evaluation                    |
| `4W`                  |  28 days | Medium-term strategy                      |
| `2M`                  |  60 days | Intermediate trend                        |
| `6M`                  | 180 days | Long-term trend                           |
| `1Y`                  | 360 days | Very long-term and maintenance evaluation |

Not every historical calculation used every window. The redesigned tracker
nevertheless established these six durations as its common analysis-window set.

The identifiers are historical labels. In particular, `6M` and `1Y` mean 180
and 360 calendar days rather than calendar months or a calendar year; an
application model need not preserve potentially misleading names.

For a historical window of `N` days ending on date `d`, the inclusive range was:

`d - (N - 1 days)` through `d`

Incomplete windows must not appear complete. Whether to suppress them,
publish a provisional result or show the available-day count remains a method
decision.

Eligibility is metric-specific. It may depend on record completion, value
presence, measurement quality, day type or the availability of an effective
weight. Calendar size must remain distinguishable from eligible-day count,
and exclusions must remain explainable. Twenty-six measured dates within a
28-day window are not a 26-day calendar window.

Date-specific inputs such as goals, day rules and expected-weight values must
use the configuration applicable to each date. Calculation methods are also
historically versioned, but the point at which a method applies is
calculation-family-specific. A method change must not silently reinterpret
earlier historical results. See [Goals and day types](goals-and-day-types.md).

## Balance and status semantics

A balance compares an observation `x` with a reference `r`. One historical
convention was:

`balance = x - r`

Under this convention, a negative nutrition balance is below the reference.
Physiological deficit uses a different orientation:

`estimated deficit = estimated expenditure - measured intake`

These results require distinct names; a generic balance whose sign changes
between contexts is ambiguous.

### Nutrition adherence

Calorie adherence compares the applicable realised intake value or range
with the target or range resolved for each date. Protein and fibre analysis
compares eligible measured intake with each applicable threshold separately.

Protein and fibre adherence excluded intentionally unmeasured dates rather
than inventing nutrient intake.

Calorie analysis developed differently. The redesigned tracker represented
an unmeasured Regular or All-in cheat through the applicable historical intake
range, producing lower and upper realised-calorie bounds. The eventual
application treatment of such bounded intake remains a calculation-method
decision.

Spreadsheet helpers sometimes contributed numeric zero for an excluded date.
That value meant only that the date did not participate in the sum; it was
not an observation or balance and must not become application state.

Daily and rolling statuses are different results. A useful status remains
interpretable in terms of its metric, window, thresholds, eligibility and
calculation method without hiding the underlying values. Threshold
definitions and nutrition measurement rules belong to
[Nutrition and activity](nutrition-and-activity.md).

### Estimated energy balance

Historical estimates combined adopted maintenance, measured intake and
derived activity energy. They were approximations rather than observed
physiological facts. The workbook used approximately 7,700 kcal as the
energy equivalent of one kilogram; any retained value should be an
identifiable method parameter, not an unexplained constant.

An energy method must also expose how it avoids counting activity already
represented in maintenance or overlap between steps and exercise. No
energy formula is currently accepted for the application.

## Weight boundaries

### Inputs and reference

Expected weight comes from the goal trajectory and is not measured,
predicted, interpolated or rolling observed weight. An observed-side value
may use a measurement, prediction or interpolation when the selected method
permits it; the source must remain visible. The redesigned tracker’s
effective-weight calculation allowed direct values, linear interpolation and,
when only one endpoint existed, carrying that endpoint forward or backward.
Whether an application analysis method should accept all of those sources is
a separate decision, and the source must remain visible. See
[Weight and projections](weight-and-projections.md).

A boundary reference may depend on the expected trajectory, analysis
window, goal mode and calculation method. The workbook stored a reference
window and margins, then could derive values for other windows. Any such
derivation must be explicit and historically identifiable.

### Historical structure and fixed margins

The boundary design distinguished downward from upward deviations and first
from outer severity. Its conceptual ordering was:

1. outer downward boundary;
2. first downward boundary;
3. expected or acceptable region;
4. first upward boundary;
5. outer upward boundary.

Margins could be asymmetric. Historical strategies used absolute kilogram
margins during weight loss and considered proportional margins for
longer-term maintenance. Their units must never be inferred.

For an absolute rule, the workbook model was:

- first down: `reference - first down margin`;
- outer down: `reference - outer down margin`;
- first up: `reference + first up margin`;
- outer up: `reference + outer up margin`.

This fixed-margin model is historical evidence, not the application’s
initial implementation contract. The redesign later adopted a provisional
buffer method for deriving margins at other windows, described below. Still
later research considered accumulated kilogram-days as a possible
replacement. A materially different method requires its own identity rather
than silently changing historical interpretation.

### Provisional multi-window derivation

The redesigned tracker later adopted a provisional method for deriving
margins at analysis windows other than the boundary’s reference window:

$$ m_s(w)=m_s(R)-b(R)+b(w) $$

where `R` is the configured reference window and `b(w)` is a window-specific
variation buffer. The same buffer was initially shared across direction and
severity. This method was deliberately provisional rather than a claim about
physiological fluctuation.

A later kg-day method was researched but not adopted. Any replacement
method was intended to receive a new historical method identity rather than
altering earlier interpretation.

### Interpretation

A first crossing suggests a deviation worth inspecting; an outer crossing
is a stronger signal that may justify review. Neither automatically changes
a goal, observation or method.

Direction is contextual. During weight loss, an upward deviation may
indicate slower loss while a downward deviation may indicate faster loss.
During maintenance, the same directions may indicate sustained gain or loss.
Meaning also depends on duration, severity, data quality and user preference.

## Signals across windows

Different windows may legitimately disagree: a recent upward crossing can
coexist with an acceptable medium-term result and a long-term downward trend.
Collapsing these results requires an explicit prioritisation rule.

The research proposed signals containing the metric, date, window, direction,
severity and supporting values. A signal is evidence for a possible review,
not a new goal.

## Maintenance and presentation

Maintenance research considered long-window weight direction, boundary
crossings, adopted maintenance calories, measured intake, activity
adjustments and measurement coverage. It should not infer a strategy change
from isolated daily weight movements.

The adopted maintenance value is an empirical strategy parameter. Revising
it is different from changing the desired weight, boundary tolerance or
calculation method. Presentation may show calculated results and their
supporting evidence, but must not independently redefine the domain
calculations.

## Explanation and provenance

An important result should be explainable from its included dates,
exclusions, effective goals and day types, weight sources, references,
thresholds, method and configuration. The inputs and intermediate reasoning
needed to explain an important result must be available or reproducible even
when they are not displayed. [Calculation history](calculation-history.md) describes the broader
historical and recalculation concerns.

Historical recalculation can answer different questions. A past result may
be recalculated using information available now, or reconstructed using
only information that was available at the historical evaluation time. The
application need not support both forms initially, but its model must
preserve the distinction rather than making them indistinguishable.

## Durable distinctions

- Analysis results do not overwrite their inputs.
- Window size and eligible-day count describe different things.
- Historical rule and method changes must not silently reinterpret earlier
  periods.
- Missing or unmeasured values are not numeric zero observations.
- Calorie adherence and estimated physiological deficit are distinct.
- Expected and observed-side weights retain different provenance.
- Boundary units, direction and severity remain explicit.
- Different windows may produce different valid results.
- A signal does not automatically change a goal.
- Private numerical parameters remain application data.

## Open domain questions

The following matters remain deliberately undecided:

- application window identities, durations and treatment of partial windows;
- metric eligibility and how coverage affects confidence or presentation;
- calorie-adherence and physiological energy formulas;
- weight resolution and the influence of predictions on confidence;
- boundary references, margin transformations and future methods;
- prioritisation, persistence and acknowledgement of simultaneous signals;
- conditions and workflows for goal or maintenance reviews;
- whether results are stored or regenerated and how corrections propagate.

These questions should be resolved through concrete analysis, review and
reporting use cases.
