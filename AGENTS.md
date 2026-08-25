# Tracker agent instructions

## Mission and scope

Build a local nutrition, activity, weight and goal-tracking application that
replaces and expands on the historical spreadsheet tracker. The application
uses a Spring Boot backend, an Angular frontend and a local SQLite database.

Domain-driven design and hexagonal architecture preserve business meaning and
keep frameworks, persistence, HTTP and presentation at the boundaries. The
spreadsheets contain valuable evidence, but they are not a technical design to
reproduce cell by cell.

## Working without file-edit permissions

When the requested work requires file changes but the current session cannot
edit the repository:

- provide the exact code for the user to copy and paste;
- omit imports and unrelated or unchanged code;
- identify the exact file and insertion, replacement or deletion location,
  using a stable code anchor when helpful;
- briefly explain what the code does;
- keep each response short enough to remain readable in a console;
- when multiple steps are required, provide only the next actionable step and
  wait for the user's result before continuing.

Commands must also be directly copyable and must state the directory from which
they should be run when it is not the repository root.

These instructions apply only when file-edit permissions are unavailable. When
the session can edit the repository, follow the normal code cycle and implement
the changes directly unless the user explicitly requests instructions instead.

## Before changing code

1. Inspect `git status` and preserve unrelated user changes.
2. Locate the owning feature or bounded context.
3. Read only the documentation relevant to the task.
4. Inspect existing code and tests at the affected boundary.
5. Identify persistence, API and public-contract effects.
6. Resolve minor ambiguity from code, tests and relevant documentation. Ask the
   user when different reasonable interpretations would materially affect business
   behaviour, architecture, persistence, APIs or public contracts.

Search the codebase instead of assuming documented paths remain current. When
documentation, tests, code and spreadsheet evidence disagree, surface the
conflict rather than silently choosing an interpretation.

## Documentation map

| Need                                                      | Read                                                                                                               |
| --------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------ |
| Project purpose                                           | [`docs/project-overview.md`](docs/project-overview.md)                                                             |
| Historical spreadsheet context - domain overview          | [`docs/domain/tracker-context/README.md`](docs/domain/tracker-context/README.md)                                   |
| Historical spreadsheet context - Daily logging            | [`docs/domain/tracker-context/daily-observations.md`](docs/domain/tracker-context/daily-observations.md)           |
| Historical spreadsheet context - Weight and interpolation | [`docs/domain/tracker-context/weight-and-projections.md`](docs/domain/tracker-context/weight-and-projections.md)   |
| Historical spreadsheet context - Goals and day types      | [`docs/domain/tracker-context/goals-and-day-types.md`](docs/domain/tracker-context/goals-and-day-types.md)         |
| Historical spreadsheet context - Nutrition and activity   | [`docs/domain/tracker-context/nutrition-and-activity.md`](docs/domain/tracker-context/nutrition-and-activity.md)   |
| Historical spreadsheet context - Boundaries and analysis  | [`docs/domain/tracker-context/analysis-and-boundaries.md`](docs/domain/tracker-context/analysis-and-boundaries.md) |
| Historical spreadsheet context - Calculation history      | [`docs/domain/tracker-context/calculation-history.md`](docs/domain/tracker-context/calculation-history.md)         |
| Domain - modelling principles                             | [`docs/domain/modelling-decisions.md`](docs/domain/modelling-decisions.md)                                         |
| General architecture intent                               | [`docs/architecture/overview.md`](docs/architecture/overview.md)                                                   |
| Backend architecture rationale                            | [`docs/architecture/backend.md`](docs/architecture/backend.md)                                                     |
| Frontend architecture rationale                           | [`docs/architecture/frontend.md`](docs/architecture/frontend.md)                                                   |
| Tests and quality gates                                   | [`docs/development/testing-and-quality.md`](docs/development/testing-and-quality.md)                               |
| Commands and workflow                                     | [`docs/development/workflow.md`](docs/development/workflow.md)                                                     |
| Source authority and external references                  | [`docs/references.md`](docs/references.md)                                                                         |

## Source authority

Use each source for the question it answers:

- current user direction defines the intended change;
- production code and tests define implemented behaviour;
- Flyway migrations define schema history;
- build files, wrappers and lockfiles define executable tooling;
- executable architecture policies and their contract tests define package,
  file, naming, declaration and dependency restrictions. A current failure
  explains how the policy applies to the violating code;
- domain documentation defines intended business meaning. This meaning can be
  updated. This project does not aim at reproducing the spreadsheet behaviour
  exactly. Its goal is also to expand and improve on it;
- historical tracker documentation and spreadsheets provide evidence about
  legacy behavior;
- architecture guides record rationale and decisions that executable checks
  cannot express. They do not define a parallel rule inventory.

Do not reverse an accepted decision incidentally. A requested change may
intentionally make current code or documentation obsolete. Do not treat
existing implementation as authority over an explicit new decision.

## Data and working-tree safety

`data/` may contain personal health information. Never commit, log, expose, use
in tests, modify or delete it without explicit user authorisation. Integration
tests use real SQLite and production Flyway migrations with isolated databases
under `backend/build/test-databases/`.

Never edit an applied or shared Flyway migration; add a new versioned migration.
Do not use destructive Git or filesystem commands unless explicitly requested.
Keep changes focused and avoid unrelated refactors, upgrades or formatting.
Do not commit generated output, dependencies, secrets, personal paths, local
databases or unrelated IDE files.

## Architecture boundaries

Domain and application code remain framework-free. Inbound and outbound ports
belong to the application core; driving and driven adapters stay outside it.
Dependencies point inward, while configuration may know implementations to wire
them.

The executable backend rules under `backend/src/architectureTest` and frontend
policy under `frontend/eslint/architecture`, activated by
`frontend/eslint.config.js`, are authoritative for declarative restrictions. Use
a failing architecture diagnostic before the rationale in
[`docs/architecture`](docs/architecture). Preserve existing bounded-context
ownership and do not introduce new cross-context dependencies without deliberate
justification.

## Tests and verification

Choose the lowest test level that proves the behaviour. Backend unit tests avoid
Spring and databases; integration tests cover HTTP, Spring, Flyway, JDBC and
SQLite; backend architecture tests enforce backend restrictions. Frontend lint
enforces its production architecture, while frontend architecture tests verify
the custom policy implementation. Frontend behavioural tests target focused
domain, application, adapter, presenter, component or routing behaviour. Add a
regression test for a defect when practical.

Formatting commands modify files; verification commands must not.

## Build, Test, and Development Commands

| Purpose                                     | Command                                  |
| ------------------------------------------- | ---------------------------------------- |
| Run complete application with copied data   | `./run.sh`                               |
| Run complete application with an empty DB   | `./run.sh --database=empty`              |
| Run complete application with personal data | `./run.sh --database=real`               |
| Debug complete application with copied data | `./run.sh --mode=debug`                  |
| Run backend with copied data                | `./gradlew :backend:bootRun`             |
| Run backend with empty data                 | `./gradlew :backend:bootRunEmpty`        |
| Run backend with personal data              | `./gradlew :backend:bootRunReal`         |
| Debug backend with copied data              | `./gradlew :backend:bootRun --debug-jvm` |
| Backend unit tests                          | `./gradlew :backend:test`                |
| Backend integration tests                   | `./gradlew :backend:integrationTest`     |
| Backend architecture tests                  | `./gradlew :backend:architectureTest`    |
| Complete backend verification               | `./gradlew :backend:check`               |
| Build backend                               | `./gradlew :backend:build`               |
| Format backend                              | `./gradlew :backend:format`              |
| Install frontend dependencies               | `./gradlew :frontend:install`            |
| Run frontend                                | `./gradlew :frontend:start`              |
| Check frontend architecture                 | `./gradlew :frontend:lint`               |
| Test frontend architecture policy           | `./gradlew :frontend:architectureTest`   |
| Verify frontend                             | `./gradlew :frontend:check`              |
| Build and verify complete repository        | `./gradlew build`                        |
| Format complete repository                  | `./gradlew format`                       |

## Code cycle

When implementing a feature, do the following:

- for implementation work, make sure you are working in an appropriate task
  branch. If the current branch is not appropriate, create or switch branches
  only when the repository workflow calls for it. Do not delete or merge
  branches unless explicitly requested.
- follow the "Before changing code" instructions
- plan the implementation, and ask the user to validate the plan if it is not
  obvious
- use TDD for meaningful behavior changes: first add the lowest-level test that
  proves the requested behavior. Add integration or architecture tests when the
  affected boundary requires them. Minimal production scaffolding may be added
  to make the test compile, but do not implement the behavior before the failing
  test exists
- then, implement the logic to make the tests pass
- use

```bash
./gradlew format
```

to format the code

- before handoff, run the complete non-mutating gate:

```bash
./gradlew build
```

It covers backend and frontend formatting checks, linting, tests and
production builds. CI runs this gate. Do not weaken a valid rule merely to
make verification pass.

- commit the changes. Use a clear and concise commit message. If the change is
  large, consider splitting it into multiple commits.
- do not push, it is not your responsibility to push commits, merge branches,
  pull requests or deploy to production. Unless you are specifically asked to
  do it, the user will do that.

## Documentation maintenance

- Do not update documentation merely because implementation progressed.
- If a requested change makes a documented fact false, ask the user for
  confirmation. If confirmed, update its owning document.
- If the user explicitly adopts or reverses a durable domain or architecture
  decision, update its canonical document.
- Do not add speculative future behavior to normative documentation.
- Do not modify `AGENTS.md` based on a single feature or inferred preference.
  Propose the change unless explicitly requested.
