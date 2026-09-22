# Frontend architecture

## Authority

The shared intent is in the [architecture overview](overview.md). The frontend's
executable policy is declared in
[`architecture.js`](../../frontend/eslint/architecture/architecture.js), implemented
by its [`rules`](../../frontend/eslint/architecture/rules), and activated by
[`eslint.config.js`](../../frontend/eslint.config.js). Its contract tests live in
[`architecture/test`](../../frontend/eslint/architecture/test).

`./gradlew :frontend:lint` checks production files and emits the relevant allowed
folder, filename, declaration or dependency information. `./gradlew
:frontend:architectureTest` verifies the custom policy implementation. Use
`./gradlew :frontend:check` to run both as part of complete frontend verification.

This guide does not repeat enforceable structures or conventions.

## Decisions outside static policy

The frontend owns models suited to its responsibilities. It does not mirror
backend packages or treat a backend transport shape as a frontend domain model.
External input is validated and translated at the adapter boundary before it
becomes frontend-owned domain or application data.

### Context ownership

Frontend contexts follow cohesive user workflows and do not have to mirror the
backend bounded contexts. The accepted target context map is:

| Context       | Responsibility                                                   |
| ------------- | ---------------------------------------------------------------- |
| Journal       | Dated observation entry, date navigation and history             |
| Strategy      | Phase, goal, rule, schedule and per-date choice management       |
| Insights      | Progress, rolling analysis, boundaries, signals and explanations |
| Training      | Future exercise planning and completion workflows                |
| Food Planning | Future food, price and meal-planning workflows                   |

Training and Food Planning remain deferred until concrete use cases justify
their implementation. A Data Management context may be introduced if repeated
import or export interaction justifies a dedicated frontend workflow.

Journal is the user-facing workflow for recording and reviewing dated
observations. A Journal page may compose information from several backend
contexts without merging their ownership in either the frontend or backend.

A page renders state and forwards browser interaction. A framework-independent
presentation model owns meaningful state transitions. When a workflow requires
asynchronous application coordination or non-trivial reactive state, a presenter
coordinates it and translates application results into presentation data.

The presenter pattern is optional: simple pages may handle local interaction
directly while respecting the same boundaries. Angular signals remain a
presentation mechanism and do not become domain state or shared application
storage.

Global styling uses semantic CSS custom properties for shared visual decisions
such as colors, spacing, radii, focus indicators and elevation. Prefixed global
recipes provide reusable presentation patterns, while page stylesheets retain
only styles that are specific to that page. Visual values remain in CSS rather
than being duplicated as TypeScript constants. Angular components are introduced
when shared markup or interaction warrants them; shared appearance alone does
not require a component abstraction.
