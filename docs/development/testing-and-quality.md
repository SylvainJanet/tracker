# Testing and quality

## Purpose

Tests provide feedback at the narrowest useful boundary: domain and application
behaviour, adapters, databases, framework wiring, presentation and architectural
dependencies. Separate suites make their requirements and failures clear.

Running one unit-test task does not imply that custom suites or production builds
also ran. Gradle task dependencies and frontend scripts are authoritative for
what each command executes.

## Backend suites

| Suite              | Source directory                    | Responsibility                                                 |
| ------------------ | ----------------------------------- | -------------------------------------------------------------- |
| `test`             | `backend/src/test/java`             | Domain and application unit tests without Spring or SQLite     |
| `integrationTest`  | `backend/src/integrationTest/java`  | Spring MVC, configuration, JDBC, SQLite and Flyway integration |
| `architectureTest` | `backend/src/architectureTest/java` | ArchUnit package, layer and dependency rules                   |

Unit tests exercise public behaviour with small fake or in-memory outbound ports.
Reusable test adapters stay in test sources; they must not become production
fallbacks. Unit tests do not start Spring or connect to SQLite.

Integration tests exercise real technical boundaries. Spring MVC slice tests
belong here because they verify Spring MVC wiring, request validation,
serialisation, status codes, headers and `ProblemDetail` translation, even when
inbound ports are mocked. Use broader application tests only for wiring or
multi-adapter collaboration.

Backend architecture tests inspect compiled production classes without starting
Spring or a database. They are the authoritative executable definition of backend
package conventions, dependency direction and structural constraints.

Frontend production architecture is enforced during linting by the policy under
`frontend/eslint/architecture` and `frontend/eslint.config.js`. Its dedicated
architecture tests verify the custom policy implementation and diagnostics. In
both applications, use the executable policy first, its current failure second,
and the architecture guides only for rationale that cannot be inferred from
either.

## SQLite integration testing

Repository and schema tests use SQLite, not a substitute database. This verifies
SQLite SQL, `STRICT` tables, type behaviour, date constraints, uniqueness and
driver behaviour. Test databases are created with the same Flyway migrations as
the application; there is no separate test schema.

Tests never connect to data/tracker.db. Database-backed integration tests use
isolated file-backed databases under `backend/build/test-databases/`,
normally one
stable file per test class. This prevents deletion, state and
parallel-execution conflicts between classes while keeping generated databases
convenient to inspect through IntelliJ.

A database-backed test class should:

1. create its parent build directory;
2. remove its previous database before the test class;
3. apply production migrations;
4. reset relevant data before each test;
5. leave the generated file available for diagnosis.

Explicit cleanup is clearer than relying on transactional rollback for these
tests.

Migration tests cover valid data and important defensive constraints. For the
current daily-record table, that includes valid leap dates, malformed or
impossible dates, unknown completion statuses, duplicate dates and required
columns. Domain code remains the primary source of business validity; database
tests protect against adapter defects, manual SQL and other clients.

## Frontend test categories

Frontend tests live beside their source and use the `*.spec.ts` suffix.

| Category     | Primary responsibility                                                     | Preferred boundary                                           |
| ------------ | -------------------------------------------------------------------------- | ------------------------------------------------------------ |
| Domain       | Calendar-date validation, value semantics and domain rules                 | Plain TypeScript without `TestBed`, HTTP or DOM APIs         |
| Application  | Use-case outcomes and calls through outbound ports                         | Direct service construction with small fakes                 |
| HTTP adapter | URLs, methods, payloads, runtime response validation and error translation | Angular HTTP testing or focused adapter tests                |
| Presenter    | Input validation, loading, record and problem states                       | Direct construction with fixed use cases and `TodayProvider` |
| Component    | Rendering, bindings, event forwarding and accessibility                    | Angular component test with presenter boundary controlled    |
| Routing      | Guards, redirects, parameters, lazy loading or route providers             | Add only when route behaviour is meaningful                  |
| End to end   | A small number of complete user journeys                                   | Full application boundary                                    |

HTTP adapter tests do not call a running backend. Error translators receive
focused tests for supported statuses, malformed problem responses and unexpected
failures. Presenter tests do not need `TestBed` when the presenter is an ordinary
class. Component tests render presentation behaviour without repeating domain,
application or backend protocol assertions.

End-to-end testing should be introduced only when a valuable complete workflow
justifies its slower and more fragile execution.

## Test design

Tests assert observable behaviour and meaningful interactions across ports, not
private implementation steps.

| Double | Use                                                         |
| ------ | ----------------------------------------------------------- |
| Fake   | Lightweight working implementation with useful state        |
| Stub   | Predetermined result for one controlled scenario            |
| Spy    | Records an interaction that is part of observable behaviour |
| Mock   | Framework-defined expectations; use selectively             |

A small object literal or class is preferable to a new mocking abstraction when
it is clearer. Reusable helpers may provide fixed dates, builders, HTTP fixtures
or database setup, but should not hide the behaviour under test. Feature-specific
fixtures do not belong in global setup.

Use the test framework’s built-in synchronous and asynchronous error assertions.
Custom matchers belong in global setup only when they are genuinely reused and
precisely typed. Real personal tracking data must not appear in tests or fixtures.

## Commands and discovery

| Command                                | Scope                                                                          |
| -------------------------------------- | ------------------------------------------------------------------------------ |
| `./gradlew :backend:test`              | Backend unit tests only                                                        |
| `./gradlew :backend:integrationTest`   | Backend integration suite                                                      |
| `./gradlew :backend:architectureTest`  | Backend ArchUnit suite                                                         |
| `./gradlew :backend:check`             | Complete backend verification, including required suites and formatting checks |
| `./gradlew :backend:build`             | Complete backend verification and packaging                                    |
| `./gradlew :frontend:check`            | Frontend formatting, lint, and tests                                           |
| `./gradlew :frontend:architectureTest` | Frontend architecture policy contract tests                                    |
| `./gradlew check`                      | Verifies the backend and frontend                                              |
| `./gradlew build`                      | Non-mutating backend and frontend verification and builds included             |
| `./gradlew :frontend:test`             | Frontend tests once                                                            |
| `./gradlew :frontend:lint`             | TypeScript, Angular template and production architecture linting               |
| `./gradlew :frontend:formatOnlyCheck`  | Frontend formatting verification                                               |
| `./gradlew :frontend:build`            | Frontend production build                                                      |

Gradle executes the requested task and its declared dependencies; a custom
source directory alone does not make its tests run. When adding a suite, wire it
into the appropriate verification lifecycle.

Use `--rerun-tasks` when investigating test discovery or summaries that would
otherwise be `UP-TO-DATE`. Test filters and Gradle task listings help confirm
which suite owns a test.

## Reports and quality gates

Every Gradle `Test` task prints a concise passed, ignored and failed summary.
Task outcomes and generated reports remain authoritative:

- HTML: `backend/build/reports/tests/<task>/index.html`;
- machine-readable results: `backend/build/test-results/<task>/`.

An `UP-TO-DATE` or cached task reused previous outputs; it did not execute tests
during that invocation. Lack of per-test console output is not evidence that a
suite was skipped.

Formatting application and verification are separate. Backend formatting uses
Spotless (`./gradlew :backend:format` to modify files); frontend formatting uses
Prettier
(`npm run format` to modify and `npm run format:check` to verify). ESLint applies
the executable frontend architecture policy to production code and templates;
the dedicated architecture suite verifies that policy's implementation and
diagnostics.

Production builds remain part of the complete gate because tests alone do not
prove that Spring packages or Angular templates compile. `build` is the
repository-wide non-mutating verification command; its Gradle task dependencies
and frontend scripts are authoritative for the exact checks it executes.

CI runs `./gradlew build --no-daemon` from a clean checkout for pushes and pull
requests targeting `master`. It uses the wrappers and committed frontend lockfile
and does not touch the personal database. Branch protection, rather than the
workflow file alone, determines whether that result blocks merging.

Coverage percentages must not replace boundary-focused assertions. A numeric
coverage threshold should be introduced only through an explicit quality decision.

## Expected tests by change

| Change                          | Expected test                                   |
| ------------------------------- | ----------------------------------------------- |
| Domain invariant                | Domain unit test                                |
| Application use case            | Application service unit test                   |
| Database mapping                | Repository integration test                     |
| Migration or constraint         | Schema integration test                         |
| HTTP endpoint                   | Controller integration test                     |
| Backend dependency boundary     | Architecture test                               |
| Frontend domain value           | Frontend domain test                            |
| Frontend use case               | Application test                                |
| HTTP DTO or problem translation | HTTP adapter test                               |
| Presenter state transition      | Presenter test                                  |
| Template behaviour              | Component test                                  |
| Critical complete workflow      | End-to-end test when the workflow justifies one |

Not every change needs every category. Place each assertion at the lowest level
that verifies the behaviour reliably, retaining only a small number of broader
wiring and workflow tests.

## Quality principles

- Protect business decisions and architectural boundaries.
- Test real infrastructure where its behaviour differs.
- Keep tests deterministic and failures understandable.
- Avoid coupling tests to private implementation details.
- Keep static checks, tests and production builds in the repository quality gate.
- Treat build files, package scripts and CI configuration as executable sources
  of truth.
