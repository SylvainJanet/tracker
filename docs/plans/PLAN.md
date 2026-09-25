# Tracker implementation plan

## Purpose

Build Tracker incrementally as a local modular monolith that can first preserve
the complete historical spreadsheet data, then reproduce its useful analysis,
and finally add planning capabilities that were impractical in the spreadsheet.

This plan tracks major capabilities and sequencing. Detailed business rules are
decided in the vertical slice that first needs them.

> This is an active implementation plan. It may repeat implemented behaviour to
> establish the starting point for remaining tickets. Canonical domain and
> architecture decisions live in the corresponding domain and architecture
> guides. Delete this plan when the tracked work is complete.

## Progress notation

- `[ ]` Not started
- `[~]` In progress
- `[x]` Complete
- `[!]` Blocked or awaiting a decision

## Agreed context map

### Backend bounded contexts

| Context       | Responsibility                                                                                                                                                 |
| ------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Journal       | User-entered dated information, including weight measurements, weight predictions, nutrition observations, measurement quality, steps and performed exercise   |
| Strategy      | Phases, goals, effective-dated policies, day-type strategies and assignments, expected trajectories, maintenance assumptions and gap-resolution policies       |
| Analysis      | Analytical workflows: obtaining context-owned data, translating it into mathematical inputs, coordinating calculations and publishing meaningful dated results |
| Import        | Spreadsheet parsing, translation, validation, dry runs, repeatable execution and reconciliation reporting                                                      |
| Training      | Future structured exercise definitions, prescriptions, plans and completed sessions                                                                            |
| Food Planning | Future foods, prices, nutritional composition, meals and meal plans                                                                                            |

Training and Food Planning are accepted future boundaries but are not created
until their first concrete use cases.

Statistics is a supporting mathematical module, not a bounded context. It
operates on indexed numerical series without knowing their units, provenance or
business meaning. Analysis accesses it through application contracts and
translates its numerical results back into Analysis-owned representations.

```mermaid
flowchart LR
    Import --> Journal
    Import --> Strategy
    Journal --> Analysis
    Strategy --> Analysis
    Analysis --> Statistics
    Training -.-> Journal
    FoodPlanning["Food Planning"] -.-> Journal
```

### Frontend contexts

| Context       | Responsibility                                                    |
| ------------- | ----------------------------------------------------------------- |
| Journal       | Dated observation entry, validation, navigation and history       |
| Analysis      | Validation and presentation of backend-published analysis results |
| Training      | Future exercise planning and completion workflows                 |
| Food Planning | Future food, price and meal-planning workflows                    |

Frontend contexts follow user workflows and do not have to mirror backend
bounded contexts. The frontend does not implement Strategy or Statistics.
Analysis treats the backend as authoritative for calculated results. A Data
Management context may be introduced later if repeated import or export
interaction justifies a dedicated workflow.

## Boundary decisions

- A calendar date composes information without making every concept one
  aggregate.
- A weight measurement is an independent dated observation within Journal.
- Logging one does not create a broader dated aggregate or completion lifecycle.
- A weight prediction is a separate Journal aggregate.
- Measured, predicted, expected, interpolated and carried weights remain
  distinguishable.
- Journal alone determines whether a recorded observation is valid.
- Strategy owns day-type definitions, schedules, rule resolution, per-date
  assignments and policies for resolving incomplete input.
- Journal may reference the strategy classification applicable to a date without
  owning its rules.
- Analysis coordinates analytical workflows. It does not own Journal
  validation, Strategy policies or reusable mathematical operations.
- Statistics receives indexed numerical data and returns mathematical results.
  It does not know about dates, weights, calories, nutrients, journaling,
  strategies or presentation.
- Analysis translates published context data into Statistics inputs and
  translates numerical outputs into meaningful dated results.
- Analysis owns calculated results and never replaces source observations,
  predictions or goals.
- Import coordinates owning contexts but does not become the owner of imported
  health or strategy data.
- Cross-context collaboration uses explicit application contracts and
  context-owned representations.
- Contexts do not access another context’s domain objects, repositories or
  persistence models.
- Direct synchronous collaboration is preferred initially. Events require a
  demonstrated asynchronous or decoupling need.
- The governed shared domain is limited to an inclusive, ordered `DateRange`
  whose meaning and invariants are identical in the collaborating contexts.
- Journal owns `Weight` and `WeightMeasurement`; they are not shared with
  Analysis.
- Frontend Journal independently applies Journal measurement rules to provide
  immediate feedback, while the backend remains authoritative.
- Frontend Analysis validates the structure and coherence of published results
  without applying Journal rules or reproducing statistical calculations.
- Internal capability modules organize a context without automatically becoming
  bounded contexts.
- Context tables remain visibly owned and avoid cross-context database foreign
  keys.
- Applied Flyway migrations are never edited.

## Delivery principles

For every meaningful behaviour slice:

1. Confirm the owning context and affected contracts.
2. Add the lowest-level failing behavioural test.
3. Add integration or architecture coverage when crossing those boundaries.
4. Implement the smallest coherent vertical slice.
5. Run `./gradlew format`.
6. Run `./gradlew build`.
7. Update documentation only when an accepted fact or decision changed.
8. Commit the verified slice with a focused message.

Real health data must never enter source control, logs, automated tests, or
committed fixtures. Tests use synthetic data and isolated SQLite databases.

## Milestone 0 — Record the domain boundaries

- [x] Update the canonical domain-modelling decisions with the accepted context
      map.
- [x] Update daily-observation and weight documentation to describe weight
      measurements as independent dated observations.
- [x] Record the target frontend context map and its independence from backend
      boundaries.
- [x] Document Journal's internal capability areas without declaring additional
      bounded contexts.
- [x] Document the shared-kernel decision.
- [x] Remove resolved context-ownership questions from the open-question lists.
- [x] Keep unresolved feature-level rules explicitly open.

### Exit criteria

The documentation consistently describes Journal, Strategy, Analysis, Import,
and the two deferred contexts without contradicting weight ownership.

## Milestone 1 — Complete Journal persistence

- [x] Keep weight measurements within Journal rather than creating a separate
      Weight context.
- [x] Implement the initial weight-measurement logging slice.
- [ ] Model daily nutrition observations: calories, protein, and fibre with
      explicit units and absence semantics.
- [ ] Model measurement quality independently from any future completion
      concept.
- [ ] Model steps so missing and zero remain distinct.
- [ ] Preserve current exercise history as a recorded label and reported energy
      estimate.
- [ ] Mark imported exercise-energy values with spreadsheet provenance and do
      not present them as measured physiological facts.
- [ ] Add a separate dated weight-prediction aggregate.
- [x] Add weight-measurement lookup by date.
- [ ] Add remaining reading, history, and correction use cases for dated
      observations.
- [ ] Decide whether later Journal use cases require a broader dated aggregate
      or explicit completion concept.
- [x] Add date-range queries needed by history and later analysis.
- [ ] Extend SQLite through new Flyway migrations.
- [x] Replace the temporary frontend daily-record experience with the Journal
      workflow.
- [x] Remove dummy frontend contexts.

### Decision gates

- Numerical ranges and storage precision for observations other than weight.
- Whether later Journal use cases require an explicit completion concept.
- If completion is introduced, whether correction requires reopening the day.
- The initial correction-history policy.

### Exit criteria

Every source daily field can be represented without converting missing,
unmeasured, estimated, or zero values into one another.

## Milestone 2 — Establish Strategy history

- [ ] Model stable phase identities and effective periods.
- [ ] Model weight-loss and maintenance modes.
- [ ] Model effective-dated goal histories without unintended gaps or overlaps.
- [ ] Represent expected-weight trajectory endpoints.
- [ ] Represent calorie targets or ranges and protein and fibre rules.
- [ ] Represent the four historical day types.
- [ ] Represent repeating day-type strategies.
- [ ] Represent per-date assignments and overrides independently from
      observations.
- [ ] Represent maintenance assumptions and weight-boundary policies separately
      from calculation methods.
- [ ] Publish a context-owned resolved-strategy result for a date.
- [ ] Define frontend ownership for entering and reviewing the relatively small
      phase and goal history when that workflow is implemented. Do not assume
      that it requires a frontend Strategy context.

### Decision gates

- Cycle-position and override semantics.
- Whether unused exceptional-day opportunities can move or accumulate.
- How erroneous historical configuration is corrected.
- Which independently changing policies require separate aggregates.

### Exit criteria

The complete historical strategy can be entered manually, and every historical
date can resolve to an identifiable phase, goal, day assignment, and applicable
policy.

## Milestone 3 — Import and reconcile spreadsheet history

- [ ] Confirm the source format and mapping without exposing personal values.
- [ ] Do not inspect or modify `../../data` without explicit authorization.
- [ ] Define a versioned source-to-domain mapping.
- [ ] Keep manual phase and goal entry separate from bulk daily-log import.
- [ ] Import per-date strategy assignments or exceptions that cannot be derived
      reliably.
- [ ] Translate spreadsheet daily rows into Journal commands.
- [ ] Distinguish measured weight from predicted or calculated spreadsheet
      values before import.
- [ ] Preserve legacy exercise labels and reported energy estimates for later
      Training translation.
- [ ] Implement a complete dry run before mutation.
- [ ] Report invalid rows, duplicates, unresolved strategy dates, and lossy
      translations.
- [ ] Make execution repeatable through import-run identity and row
      fingerprints.
- [ ] Reject conflicting reruns instead of silently overwriting data.
- [ ] Produce a reconciliation report with source, accepted, rejected, and
      skipped counts.
- [ ] Verify the importer using only synthetic fixtures.

### Exit criteria

The 300-plus historical daily rows can be imported without loss of meaning, every
accepted row is traceable to its source, and rerunning the import cannot create
duplicates.

## Milestone 4 — Build the analysis foundation

- [x] Establish Analysis-owned dated-series and dated-value representations
      without sharing Journal’s weight domain.
- [x] Obtain weight data through Journal application contracts and translate it
      into Analysis-owned input.
- [x] Derive stable timeline day numbers from calendar dates while retaining the
      dates in published results.
- [x] Create the business-agnostic Statistics supporting module and its inbound
      rolling-average contract.
- [x] Wire Analysis to Statistics through that contract.
- [x] Publish the requested timeline, range and weight-measurement data while
      deliberately returning no rolling averages until their calculation ticket.
- [ ] Model calculation-method identity and effective history independently from
      goals.
- [ ] Resolve expected weight from the applicable strategy trajectory.
- [ ] Resolve effective weight from direct measurements and predictions.
- [ ] Implement linear interpolation with explicit endpoint provenance.
- [ ] Implement carry-forward and carry-backward only where the selected method
      permits them.
- [ ] Expose whether a weight is measured, predicted, interpolated or carried.
- [ ] Retain full intermediate precision and round only at defined boundaries.
- [x] Generate results on demand initially.
- [ ] Keep inputs and intermediate reasoning available for explanation.

### Decision gates

- Whether predictions may participate in every weight calculation.
- Method effective-date semantics for each calculation family.
- Treatment of corrections when recalculating historical results.
- When, if ever, calculated snapshots need persistence.

### Exit criteria

A weight or expected-weight result for any date can explain its source,
applicable strategy, and calculation method.

## Milestone 5 — Reproduce weight progress and boundaries

- [ ] Implement reusable indexed rolling-average calculations in Statistics.
- [ ] Have Analysis select the required windows and translate dated weight data
      into indexed Statistics inputs.
- [ ] Translate Statistics results back into dated rolling-weight results with
      stable timeline indexes.
- [ ] Compare rolling results with expected trajectories.
- [ ] Implement direction- and severity-specific boundary policies.
- [ ] Version boundary calculation methods independently from policies.
- [ ] Represent simultaneous signals across different windows.
- [ ] Keep signals as evidence for review rather than automatic strategy
      changes.
- [ ] Support weight-loss and maintenance interpretations.
- [ ] Explain references, margins, sources, windows and methods.

### Decision gates

- Accepted rolling windows, partial-window behaviour and rounding.
- Whether predictions may participate in rolling-weight calculations.
- Accepted boundary method and parameters.
- Treatment of incomplete coverage.
- Signal prioritization and acknowledgement.
- Conditions for proposing a goal or maintenance review.

### Exit criteria

Weight progress can be evaluated over the required windows without hiding
provenance or silently reinterpreting historical rules.

## Milestone 6 — Reproduce nutrition analysis

- [ ] Resolve date-specific calorie, protein and fibre targets.
- [ ] Obtain Strategy-owned eligibility and gap-resolution decisions before
      requesting mathematical analysis.
- [ ] Implement daily calorie adherence.
- [ ] Implement minimum-oriented protein and fibre evaluation.
- [ ] Reuse Statistics rolling operations for the accepted nutrition windows.
- [ ] Keep calendar-window size distinct from eligible-day coverage.
- [ ] Support intentionally unmeasured days without inventing observations.
- [ ] Explain included dates, exclusions, thresholds and applicable goals.

### Decision gates

- Final window identities and partial-window behaviour.
- Calorie treatment for intentionally unmeasured exceptional days.
- Protein and fibre threshold equality rules.
- Confidence or presentation rules for incomplete coverage.

### Exit criteria

The application reproduces the accepted spreadsheet nutrition conclusions and
explains differences caused by deliberately changed rules.

## Milestone 7 — Deliver the Analysis experience

- [ ] Build a summary dashboard from Analysis queries.
- [ ] Show current strategy and progress.
- [ ] Show recent and longer-window nutrition results.
- [ ] Show weight trajectory and boundary signals.
- [ ] Make important results expandable into explanations.
- [ ] Distinguish missing, unmeasured, provisional, and complete information.
- [ ] Keep presentation composition outside domain ownership.

### Exit criteria

The primary spreadsheet conclusions are accessible without inspecting raw
calculation tables.

## Milestone 8 — Expand beyond the spreadsheet

### Training

- [ ] Define structured exercises and reusable prescriptions.
- [ ] Define planned sessions and plan versions.
- [ ] Record completed and missed sessions explicitly.
- [ ] Translate legacy exercise labels without discarding their original
      provenance.
- [ ] Decide how step and exercise overlap affects energy estimates.

### Food Planning

- [ ] Define foods and nutritional composition.
- [ ] Add price histories and comparison strategies.
- [ ] Define meal possibilities and plans.
- [ ] Make recording planned food as consumed an explicit action.
- [ ] Keep calculated plan totals separate from observed daily intake.

### Data management

- [ ] Add export and backup workflows.
- [ ] Add a Data Management frontend context if repeated user interaction
      justifies it.
- [ ] Preserve local-first privacy and require an explicit decision before any
      external transmission.

## Completion criteria for the initial spreadsheet replacement

The initial replacement is complete when:

- all historical daily and strategy data can be preserved;
- daily information can be entered and corrected without the spreadsheet;
- the accepted nutrition and weight analyses are reproduced;
- results retain source, rule, and method provenance;
- the Journal and Analysis workflows cover routine use;
- `./gradlew build` passes;
- real personal data remains local and outside version control.
