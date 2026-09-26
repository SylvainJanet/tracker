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

### Analysis result validation

The Analysis HTTP adapter validates the transport shape before translating it
into an application-owned outbound outcome. The Analysis application constructs
the Analysis domain result, which validates semantic coherence. It may derive an
expected relationship between published fields—for example, between a date and
timeline index—solely to reject an incoherent result. It preserves the
backend-published value and does not reproduce the statistical calculation.

The application translates the validated domain result into its inbound use-case
result. The Angular adapter then maps that result into presentation-owned views
and workflow state. Each boundary retains its own representation.

### Presentation workflows

A page owns browser and Angular integration. A framework-independent model owns
meaningful workflow state and immutable transitions without invoking use cases
or subscribing to external work. When asynchronous coordination or reactive
state is non-trivial, a presenter mediates between the page, model and
application and maps application results into presentation-owned data.

Framework-local interaction state remains outside the model unless it has
workflow meaning. The presenter pattern is optional for simple local
interaction, but pages still respect the application boundary.

### Presentation mapping and construction

Every layer owns its representation. Backend transport DTOs, frontend
application results, presentation views and domain objects are not reused merely
because their current fields happen to match.

A presenter mapper translates application results into presentation views when
that translation is a meaningful boundary, contains nested data or would
otherwise obscure presenter orchestration. When a dedicated mapper exists, the
presenter uses it rather than duplicating the translation. The mapper remains
pure and receives a focused unit test.

A gateway is itself the translation boundary between backend-controlled data and
a frontend-owned outbound-port outcome. A separate HTTP-contract mapper is
introduced only when that translation becomes sufficiently complex or reusable;
a mapper is not required for every field copy. The same restraint applies inside
application services.

TypeScript object literals use named properties and construct complete immutable
structures, so builders are not the default. Introduce a builder or factory only
when it provides meaningful staged construction, defaults, normalization or
validation. Do not add one merely because an object has several fields or to
reproduce a Java construction convention.

### Presentation testing

Test each presentation responsibility at its narrowest boundary. Page tests use
a controlled presenter and cover semantic rendering, accessibility and browser
interaction without retesting model transitions, application coordination or
CSS layout. The [testing guide](../development/testing-and-quality.md) records
the broader frontend testing strategy.

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
