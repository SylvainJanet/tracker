# Frontend architecture

## Authority

The [architecture overview](overview.md) records shared intent. Executable
frontend policy defines exact structure and dependencies; this guide records
decisions that policy cannot explain rather than repeating enforceable rules.

## Decisions outside static policy

The frontend owns models suited to its responsibilities. It does not mirror
backend packages or treat a backend transport shape as a frontend domain model.
External input is validated and translated at the adapter boundary before it
becomes frontend-owned domain or application data.

### Context ownership

Frontend contexts follow cohesive user workflows and do not have to mirror the
backend bounded contexts. The accepted target context map is:

| Context       | Responsibility                                                    |
| ------------- | ----------------------------------------------------------------- |
| Journal       | Dated observation entry, validation, navigation and history       |
| Analysis      | Validation and presentation of backend-published analysis results |
| Training      | Future exercise planning and completion workflows                 |
| Food Planning | Future food, price and meal-planning workflows                    |

Frontend Journal owns its representations of weight and weight measurements. It
applies the same measurement rules as backend Journal so that it can provide
immediate feedback, while the backend remains authoritative. These concepts are
not shared with frontend Analysis.

Frontend Analysis owns a model of a published analysis result. It validates the
result’s structure and coherence, including valid and ordered dates, matching
date ranges and timeline indexes, and finite positive result values. It does not
enforce Journal measurement rules, resolve missing observations, select strategy
policies, reproduce statistical calculations or use Journal domain objects.

The frontend does not implement Strategy or Statistics. Analysis consumes the
resolved results published by the backend.

Training and Food Planning remain deferred until concrete use cases justify
their implementation. A Data Management context may be introduced if repeated
import or export interaction justifies a dedicated frontend workflow.

Journal is the user-facing workflow for recording and reviewing dated
observations. A Journal page may compose information from several backend
contexts without merging their ownership in either the frontend or backend.

### Shared calendar kernel

Frontend Journal and Analysis share only `DateRange`, representing inclusive,
ordered civil-calendar-date bounds. The shared calendar-date validation used to
construct and inspect those bounds does not introduce a broader shared domain.

The shared calendar kernel contains no weight, measurement, completion,
analysis-window, strategy or presentation concepts.

Analysis separately owns timeline indexes derived from elapsed calendar days. It
retains dates for selection and display and indexes for graph positioning without
collapsing gaps.

### Analysis results and presentation

The backend is authoritative for published analysis values. The frontend validates
external result structure and semantic coherence before treating it as
frontend-owned data. It may verify relationships between published fields, such
as dates and timeline indexes, but does not replace values or reproduce backend
calculations.

Each boundary owns representations appropriate to its responsibilities.
Translation abstractions are introduced only when they protect ownership or make
a non-trivial transformation clearer; matching field shapes alone do not require
a mapper, builder or additional model.

Browser and framework concerns remain at the presentation boundary, while
workflow and application behaviour remain independently testable. The
[testing guide](../development/testing-and-quality.md) defines the corresponding
test boundaries.

### Styling

Global styling uses semantic CSS custom properties for shared visual decisions
such as colors, spacing, radii, focus indicators and elevation. Prefixed global
recipes provide reusable page, panel, status, form, button and detail patterns.

A page starts with the existing shared tokens and recipes. Its stylesheet keeps
only layout or appearance that is specific to that page, such as an
analysis-specific summary or data table. Promote a pattern to shared styling
when more than one feature genuinely uses the same visual responsibility; do
not extract speculative abstractions.

Visual values remain in CSS rather than being duplicated as TypeScript
constants. Angular components are introduced when shared markup or interaction
warrants them; shared appearance alone does not require a component abstraction.
