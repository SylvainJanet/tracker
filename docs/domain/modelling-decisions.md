# Domain modelling decisions

## Purpose

This document records domain decisions that should survive changes in storage,
APIs and presentation. It defines the currently accepted bounded contexts while
leaving detailed aggregates and use cases to emerge from concrete
responsibilities and interactions.

The central aim is to preserve meaningful distinctions without turning the
spreadsheet layout or the first implemented feature into the application’s
permanent model. Context boundaries may evolve, but changes must be deliberate
and preserve the meaning and provenance of existing information.

## Boundary principles

Bounded contexts follow cohesive language, rules, lifecycle and ownership. They
must not be derived mechanically from spreadsheet sheets, database tables, REST
resources, routes, pages, modals, CRUD operations or package names. Creating and
editing the same business concept are normally use cases within its owning
context, not separate contexts.

A new context becomes useful when a responsibility has sufficiently independent
terminology, invariants, lifecycle, persistence needs, use cases or rate of change.
Symmetry in a package tree is not a reason to introduce one.

Coordination between several areas does not automatically create another
context. Coordination may remain an application workflow or presentation
composition until it develops stable business meaning, state and rules of its
own.

A calendar date is a common composition dimension. Observations, plans,
predictions, strategy and analysis can appear together for one date while
retaining distinct meanings and lifecycles. Sharing a date or appearing on the
same screen does not make those concepts one aggregate.

The [tracker domain overview](tracker-context/README.md) contains a provisional
concept map. Its relationships remain navigation aids; context ownership is
defined below.

## Bounded contexts

The initial backend consists of four bounded contexts:

| Context  | Responsibility                                                                                                                                               |
| -------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Tracking | User-entered dated information, including weight measurements, weight predictions, nutrition observations, measurement quality, steps and performed exercise |
| Strategy | Phases, goals, effective-dated policies, day-type strategies and assignments, expected-trajectory definitions, maintenance assumptions and boundary policies |
| Analysis | Versioned calculation methods and derived values such as expected or resolved weights, thresholds, rolling evaluations, boundaries, signals and explanations |
| Import   | Spreadsheet parsing, translation, validation, dry runs, repeatable execution and reconciliation reporting                                                    |

Training and Food Planning are accepted future bounded contexts. Training will
own structured exercises, prescriptions, plans and completed sessions. Food
Planning will own foods, prices, nutritional composition and meal plans. Neither
context should be introduced until a concrete use case requires its model.

Until Training is implemented, Tracking preserves the historical
performed-exercise label and reported energy estimate. The estimate retains its
provenance and must not be presented as a measured physiological fact. Later
translation to structured Training concepts must not discard the imported
representation.

Import coordinates the owning contexts but does not own imported observations,
predictions, strategies or goals. The relatively small phase and goal history
may be entered manually in Strategy while the larger daily history is imported
automatically.

### Context collaboration

Strategy owns day-type definitions, schedules, resolution and per-date
assignments. Tracking may reference the stable strategy classification
applicable to a date, but it does not copy or redefine the associated rules.

Analysis consumes explicit representations published by Tracking and Strategy.
Its models distinguish observations, predictions, goals, policies, methods and
derived results rather than sharing another context’s internal domain objects.

Contexts collaborate through application contracts and context-owned
representations. They do not access another context’s repositories or
persistence representations. Direct synchronous collaboration is preferred
initially; events should be introduced only when a concrete workflow requires
independent or asynchronous consumers.

### Internal capability modules

A bounded context may contain several aggregates and capability modules without
those modules becoming subordinate bounded contexts. Tracking may, for example,
contain journaling, nutrition, activity, weight prediction, correction and
provenance capabilities.

An internal module becomes a candidate for extraction only when it develops
sufficiently independent terminology, invariants, lifecycle, persistence,
contracts or rate of change. Internal package symmetry alone is not a reason to
extract it.

## Kinds of information

These categories retain different meanings and provenance:

| Kind                   | Meaning                                                                               | Examples                                           |
| ---------------------- | ------------------------------------------------------------------------------------- | -------------------------------------------------- |
| Actual observation     | Information recorded about what occurred                                              | Measured weight, intake, steps, completed exercise |
| Plan                   | An intention for a date or period                                                     | Planned day type, exercise or meal                 |
| Prediction             | A manually entered estimate                                                           | Predicted future weight                            |
| Calculated expectation | A value derived from goals or rules                                                   | Expected weight, nutrition threshold               |
| Calculated result      | A value derived from observations, predictions, goals, rules or other resolved inputs | Interpolated weight, balance, status or signal     |

A plan may exist without an observation. Carrying it out should create or update
actual information through an explicit action rather than silently treating the
plan as fact. Predictions remain user inputs even when calculations use them;
they are neither observations nor expectations. Calculations must not replace
their source values, and presentation must keep origins understandable.

## Calendar dates and today

A tracker date is a civil calendar day without a time or time zone.

The domain defines calendar-date validity. An adapter may pass external input to
the domain validator without taking ownership of that rule.

Rules that depend on today should receive the current date explicitly rather than
obtaining it implicitly. The current date is an environmental input; domain or
application rules interpret it rather than owning its acquisition. This keeps
date-dependent decisions explicit and deterministic in tests.

## Journal observations

Journal composes dated information for recording and review without making it
one aggregate. Weight measurements are independent observations within Tracking;
their inclusion does not introduce a general daily-record aggregate or completion
lifecycle.

Later nutrition, activity or completion use cases must establish their own
ownership and lifecycle rules rather than inherit them from the spreadsheet
layout.

Manually entered weight predictions remain a separate Tracking aggregate.
Expected, interpolated, carried and otherwise derived weights belong to Analysis.

## Separate domain lifecycles

Goals and calculation methods also represent different decisions. A goal states
what is intended; a method states how a derived value is calculated. They may
both need historical resolution, but adopting one must not mutate or imply a
change to the other.

Likewise, a weight measurement, nutrition observation, prediction, planned day
assignment and calculated result can concern the same date without sharing an
aggregate or transition lifecycle.

## Shared concepts and value objects

A domain-specific primitive should become a value object when its meaning,
validation or operations provide real safety. Calendar date is the current
example.

Value objects should not be introduced solely to wrap every primitive or prepare
for imagined reuse. The simplest model that preserves established distinctions
is preferred.

The application does not initially define a generic shared `Measurement`.
Measured weight, target weight, expected weight and weight deviation have
different meanings even though they use the same physical unit. The same
distinction applies to observed intake, nutritional targets and calculated
balances.

Each context initially owns its semantic value objects and business validation.
Small duplicated guards are preferable to coupling contexts through an
abstraction whose shared meaning is not yet established. Missing or
intentionally unmeasured information is represented outside a numeric value
rather than by constructing a value object with `null`.

A small governed shared kernel may be proposed later if several contexts
demonstrate identical requirements for a primitive such as a unit-safe
quantity. It must contain only genuinely shared representation and operations,
not provenance, goals, measurement quality, business ranges or context-specific
validation. Introducing it requires an explicit domain and architecture
decision.

## Architecture alignment

Ports, services, repositories, gateways, mapping, public contracts, shared code,
events, framework configuration and frontend composition are architectural
concerns rather than consequences of the domain model. Executable architecture
policies define enforceable restrictions. Rationale not expressed by those
policies is recorded in the
[architecture overview](../architecture/overview.md),
[backend guide](../architecture/backend.md) and
[frontend guide](../architecture/frontend.md). Domain documentation does not
duplicate either source.

## Deliberately open modelling questions

The following areas remain unresolved:

- detailed aggregate boundaries within Tracking and Strategy;
- aggregate boundaries for structured exercise, foods and meal planning;
- whether different plans share an abstraction;
- representation of planned, overridden and resolved day types;
- whether later Journal use cases require a broader dated aggregate or an
  explicit completion concept;
- audit requirements for corrected observations;
- correction of erroneous effective-dated strategy information;
- failure and consistency rules for operations involving several contexts;
- translation of historical exercise entries into future Training concepts;
- whether repeated primitive behaviour eventually justifies a deliberately
  governed shared kernel.

Agents must not settle these questions incidentally while implementing an
unrelated feature.

## Evolution of the domain model

This document is expected to evolve as new use cases reveal domain
responsibilities and deliberate modelling decisions are made.

New features may refine existing concepts, establish ownership or lifecycle
boundaries that are currently open, or introduce new domain distinctions. When
such a decision is deliberately adopted, update the relevant section of this
document rather than treating the current model as fixed.

When an open modelling question is resolved, record the resulting durable
decision in the appropriate section and remove or narrow the corresponding open
question.

Implementation progress alone does not establish a new modelling decision.
Documentation changes remain subject to the maintenance policy in the root
`AGENTS.md`.
