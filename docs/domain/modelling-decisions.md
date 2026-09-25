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

The accepted near-term backend context map defines four bounded contexts.
Accepting a boundary records ownership and collaboration decisions; it does not
require creating an empty module before a concrete use case needs it.

| Context  | Responsibility                                                                                                                                               |
| -------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Journal  | User-entered dated information, including weight measurements, weight predictions, nutrition observations, measurement quality, steps and performed exercise |
| Strategy | Phases, goals, effective-dated policies, day-type strategies and assignments, expected trajectories, maintenance assumptions and gap-resolution policies     |
| Analysis | Analytical workflows that obtain context-owned data, apply relevant policies, coordinate mathematical operations and publish meaningful dated results        |
| Import   | Spreadsheet parsing, translation, validation, dry runs, repeatable execution and reconciliation reporting                                                    |

Statistics is a business-agnostic supporting module rather than a bounded
context. It owns reusable indexed numerical operations and their mathematical
invariants without knowing the units, provenance or business meaning of its
inputs. Analysis translates meaningful data into Statistics inputs and
translates its numerical outputs back into Analysis-owned results.

Training and Food Planning are accepted future bounded contexts. Training will
own structured exercises, prescriptions, plans and completed sessions. Food
Planning will own foods, prices, nutritional composition and meal plans. Neither
context should be introduced until a concrete use case requires its model.

Until Training is implemented, Journal preserves the historical
performed-exercise label and reported energy estimate. The estimate retains its
provenance and must not be presented as a measured physiological fact. Later
translation to structured Training concepts must not discard the imported
representation.

Import coordinates the owning contexts but does not own imported observations,
predictions, strategies or goals. The relatively small phase and goal history
may be entered manually in Strategy while the larger daily history is imported
automatically.

### Context collaboration

Strategy owns day-type definitions, schedules, resolution policies and per-date
assignments. Journal may reference the stable strategy classification applicable
to a date, but it does not copy or redefine the associated rules.

Analysis consumes context-owned representations published by Journal and
Strategy. It does not reconstruct their domain objects or repeat validation
owned by the publishing context. It translates published values into
Analysis-owned inputs and preserves their meaning and provenance in its results.

Journal determines whether a recorded observation is valid. Strategy determines
which effective-dated policy applies when missing or incomplete information
requires a decision. Analysis coordinates the application of that policy without
turning observations, policies or calculated values into a shared domain model.

Analysis delegates reusable numerical work to Statistics. Statistics receives
only the indexes, values, windows and other mathematical parameters required by
the requested operation. It does not select eligible business data, resolve
gaps, attach units or dates, or decide how a numerical result should be
interpreted.

Contexts collaborate through application contracts and context-owned
representations. They do not access another context’s domain objects,
repositories or persistence representations. Direct synchronous collaboration
is preferred initially; events should be introduced only when a concrete
workflow requires independent or asynchronous consumers.

### Internal capability modules

A bounded context may contain several aggregates and capability modules without
those modules becoming subordinate bounded contexts. Journal may, for example,
contain journaling, nutrition, activity, weight prediction, correction and
provenance capabilities.

An internal module becomes a candidate for extraction only when it develops
sufficiently independent terminology, invariants, lifecycle, persistence,
contracts or rate of change. Internal package symmetry alone is not a reason to
extract it.

Not every separately packaged capability is therefore a bounded context.
Statistics is separate because its mathematical language and operations are
reusable without a business model or lifecycle of their own.

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

Analysis defines a timeline day number derived from calendar dates. Day 1 is the
date of the first recorded weight, and each later number is the elapsed number
of calendar days from that origin plus one. Missing measurements do not collapse
or renumber the timeline. Analysis owns this derivation and publishes both the
calendar date and day number. Presentation clients preserve the published value;
they may verify its relationship with the date but do not independently replace
it.

## Journal observations

Journal composes dated information for recording and review without making it
one aggregate. Weight measurements are independent observations within Journal;
their inclusion does not introduce a general daily-record aggregate or completion
lifecycle.

Later nutrition, activity or completion use cases must establish their own
ownership and lifecycle rules rather than inherit them from the spreadsheet
layout.

Manually entered weight predictions remain a separate Journal aggregate.
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
validation or operations provide real safety. Value objects should not be
introduced solely to wrap every primitive or prepare for imagined reuse.

The contexts deliberately share only a small governed `DateRange` concept,
representing inclusive, ordered civil-calendar-date bounds.

Journal owns `Weight` and `WeightMeasurement`. It validates recorded weights
and owns the lifecycle and identity of weight observations.

Analysis consumes weight data through published application contracts. It does
not share or reconstruct Journal’s weight domain objects and does not reapply
Journal’s measurement rules. Analysis may enforce its own input and result
requirements, such as coherent ranges, ordered unique indexes, finite positive
result values and valid analysis windows.

Strategy may represent target weights or weight-related policies using
Strategy-owned concepts. A target, observation and calculated result remain
different concepts even when they use the same physical unit.

Shared domain objects remain internal implementation concepts. Cross-context
application contracts and HTTP contracts use context-owned representations and
do not expose shared-kernel types.

Missing or intentionally unmeasured information is represented outside a
numeric value rather than by constructing a value object with `null` or using
numeric zero.

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

- detailed aggregate boundaries within Journal and Strategy;
- aggregate boundaries for structured exercise, foods and meal planning;
- whether different plans share an abstraction;
- representation of planned, overridden and resolved day types;
- whether later Journal use cases require a broader dated aggregate or an
  explicit completion concept;
- audit requirements for corrected observations;
- correction of erroneous effective-dated strategy information;
- failure and consistency rules for operations involving several contexts;
- translation of historical exercise entries into future Training concepts;

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
