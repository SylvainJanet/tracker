# Project overview

## Product purpose

Tracker is a private, local-first application for recording and understanding
nutrition, weight, physical activity and related goals over time.

Its purpose is not merely to store daily values. It must help evaluate whether  
longer-term behaviour is consistent with the user’s goals while accounting for
incomplete information, planned variations, changing strategies and the
difference between measured facts and estimates.

The long-term product scope includes the following capabilities; not all are
currently implemented.

- recording daily weight, calorie intake, protein, fibre, steps and exercise;
- distinguishing complete, incomplete, accurate and intentionally unmeasured
  information;
- planning and evaluating different kinds of days, including regular and
  exceptional cheat days;
- managing goals and rules that change over time;
- estimating expected weight trajectories;
- interpolating missing weights and incorporating manually entered
  predictions;
- evaluating nutrition and weight over several rolling time windows;
- detecting meaningful deviations from expected weight boundaries;
- evaluating maintenance as well as weight-loss phases;
- planning exercise;
- managing foods, prices and meal possibilities;
- helping compare the nutritional and financial usefulness of foods;
- presenting calculations and conclusions in an understandable and auditable
  form.

Daily variation is expected. Calories, protein, fibre, weight and activity
should generally be interpreted over appropriate time windows rather than by
treating every isolated day as a success or failure.

## From spreadsheet to application

The project originated in a progressively more sophisticated Numbers workbook.
That workbook contains historical data and significant domain knowledge.
Spreadsheet complexity became a maintenance/testing/extensibility problem.
The application replaces the implementation, not the knowledge.
Migration should be incremental.

The detailed inventory lives in [the tracker context](domain/tracker-context/README.md).

A redesigned workbook was started to separate raw observations, rule resolution,
intermediate calculations and published results. This improved auditability but
also demonstrated the limitations of continuing with a spreadsheet.

Reimplementing the useful behaviour already present in the spreadsheet is only
the first stage, not the final product scope. The application should also add
capabilities that the workbook does not currently provide, including explicit
pricing strategies and broader exercise management. Further related concerns
may be introduced as concrete needs emerge and their domain boundaries become
clear.

The spreadsheet remains useful as:

- a record of the original data and behaviour;
- a source of domain terminology;
- evidence of the calculations and edge cases already considered;
- a reference when validating future application behaviour.

Its sheets, columns and formulas are not an application architecture to
reproduce. Business meaning should be preserved, but it should be remodelled
using explicit domain concepts, use cases and tests, with historical rules and
methods represented explicitly where required.

Features should be implemented as coherent vertical slices instead of
attempting to reproduce the entire workbook in one step.

## Product qualities

The application should favour the following qualities.

### Historical correctness

Goals, strategies, rules and calculation methods can change over time. Historical
records and results must remain interpretable according to the rules and methods
that applied to them; later changes must not silently reinterpret the past.

Rules that vary over time should therefore have explicit effective periods or
another equally clear historisation mechanism.

### Explicit uncertainty

Measured values, predicted values, interpolated values and missing values do not
have the same meaning. The model must preserve these distinctions instead of
reducing them to a single nullable number.

Likewise, an intentionally unmeasured day is different from an incomplete day or
a value that was accidentally omitted.

### Auditability

Important results should be explainable from their inputs, applicable rules and
calculation method.

The spreadsheet used separate resolution, subcalculation and result layers to
improve traceability. The application does not have to reproduce those layers
literally, but it must preserve the ability to understand why a result was
produced.

### Domain clarity

Business concepts should be represented explicitly rather than hidden in booleans,
magic values, presentation states or persistence conventions.

Shared domain concepts should use clear and consistent terminology across
implementation and documentation, while allowing individual bounded contexts to
use models appropriate to their responsibilities.

### Incremental design

Unknown future requirements should not be modelled speculatively. New aggregates,
bounded contexts, events and abstractions should be introduced when their
responsibilities and interactions are sufficiently understood.

At the same time, existing concepts should not be forced into an unsuitable
model merely to avoid adding a new boundary.

## Local-first and privacy constraints

The application is currently intended for personal, single-user use and stores
sensitive health and behavioural information.

Personal application data must remain outside version control. In particular:

- local database files must be ignored;
- real health data must not appear in automated tests, fixtures or examples;
- ignored development snapshots may contain personal data for manual local
  testing, but must remain local and disposable;
- logs and error messages must not expose unnecessary personal values;
- generated database and test artefacts must not be committed;
- production data must never be reused as test data;
- external storage, telemetry or transmission of personal data must not be
  introduced without an explicit decision.

Tests should use isolated synthetic data.

The application may use local frontend and backend processes, but it should
not require a hosted external service for its normal operation.

## Relationship between documentation and implementation

Current source code, tests, database migrations and build configuration describe
how the application is implemented.

The domain documentation describes the intended business meaning and constraints.

The legacy spreadsheets provide historical and analytical context, but their
implementation details do not override explicit application decisions.
