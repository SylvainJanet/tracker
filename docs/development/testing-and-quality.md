# Testing and quality

## Purpose

Tests provide feedback at the narrowest useful boundary: domain and application
behaviour, adapters, databases, framework wiring, presentation and architectural
dependencies. Separate suites make their requirements and failures clear.

Running one unit-test task does not imply that custom suites or production builds
also ran. Gradle task dependencies and frontend scripts are authoritative for
what each command executes.

## Backend test boundaries

Unit tests exercise public behaviour with small fake or in-memory outbound ports.
Reusable test adapters stay in test sources; they must not become production
fallbacks. Unit tests do not start Spring or connect to SQLite.

Integration tests exercise real technical boundaries. Spring MVC slice tests
belong here because they verify Spring MVC wiring, request validation,
serialisation, status codes, headers and `ProblemDetail` translation, even when
inbound ports are mocked. Use broader application tests only for wiring or
multi-adapter collaboration.

Architecture tests inspect compiled production classes without starting Spring
or a database. They are the authoritative executable definition of backend
structural constraints. Frontend production architecture is likewise enforced
by executable lint policy whose own contract tests protect its behaviour and
diagnostics. In both applications, use the current policy failure first and the
architecture guides only for rationale it cannot express.

## SQLite integration testing

Repository and schema tests use SQLite, not a substitute database. This verifies
SQLite SQL, `STRICT` tables, type behaviour, date constraints, uniqueness and
driver behaviour. Test databases are created with the same Flyway migrations as
the application; there is no separate test schema.

Tests never connect to data/tracker.db. Database-backed integration tests use
isolated file-backed databases under `backend/build/test-databases/`, normally
one stable file per test class. This prevents deletion, state and
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

Migration tests cover valid data and important defensive constraints.

Domain code remains the primary source of business validity. Database tests
protect against adapter defects, manual SQL and other clients.

## Frontend test categories

Frontend tests live beside their source and use the `*.spec.ts` suffix.

| Category            | Primary responsibility                                                     | Preferred boundary                                                 |
| ------------------- | -------------------------------------------------------------------------- | ------------------------------------------------------------------ |
| Domain              | Calendar-date validation, value semantics and domain rules                 | Plain TypeScript without `TestBed`, HTTP or DOM APIs               |
| Application         | Use-case outcomes and calls through outbound ports                         | Direct service construction with small fakes                       |
| HTTP adapter        | URLs, methods, payloads, runtime response validation and error translation | Angular HTTP testing or focused pure-validator tests               |
| Presentation model  | Workflow states, accepted and ignored events, and valid transitions        | Direct immutable-model construction without Angular or RxJS        |
| Presentation mapper | Translation from application results into presentation-owned views         | Direct invocation of the pure mapper                               |
| Presenter           | Use-case coordination, effects, loading, result and failure handling       | Direct construction with controlled use cases and no `TestBed`     |
| Component           | Semantic rendering, bindings, event forwarding and accessibility           | Real component and template with the presenter boundary controlled |
| Composition         | Production routes, provider graph and component creation                   | Small `TestBed` or `RouterTestingHarness` smoke test               |
| Routing             | Guards, redirects, parameters, nesting and meaningful navigation behaviour | Focused route or component test                                    |
| End to end          | A small number of complete user journeys                                   | Full application boundary                                          |

Direct model, mapper, application and presenter tests prove behaviour without an
Angular component fixture or DOM. A presenter that can be constructed directly does not
need `TestBed`, even when it uses Angular signals or forms.

Component tests use the real component and template with a controlled fake
presenter. They prove two directions:

- presenter state produces the minimum meaningful semantic DOM;
- a browser event reaches the intended presenter or page operation.

As a default, add one minimal rendering assertion per substantially different
state and one interaction assertion per distinct user intent. States that have
the same representation do not require separate component tests when their
differences are already covered by presenter tests.

Prefer semantic content, roles, labels or stable test identifiers over CSS
classes, element positions or exact DOM structure. Component tests do not verify
layout, visual styling, Angular framework behaviour, presenter state transitions,
domain validation or backend calls.

A feature with non-trivial route-level providers should normally have one small
composition smoke test. It uses the production route and providers while
replacing external technical boundaries, such as the HTTP backend, with their
test implementations. Successfully navigating to and creating the feature is
the useful assertion: Angular must resolve the provider graph, create the
component and instantiate its template.

Prefer that composition test over separate tests asserting that every injection
token resolves. Add dedicated configuration tests only when configuration has
behaviour of its own, such as selecting between implementations.

Static path-to-component declarations do not need dedicated routing tests.
Test routing separately when application behaviour depends on parameters, query
parameters, guards, redirects, nesting, lazy loading or route-level providers.
A route-based composition test may cover routing, dependency injection and
component creation together.

HTTP adapter tests do not call a running backend. Error translators receive
focused tests for supported statuses, malformed problem responses and unexpected
failures.

End-to-end testing should be introduced only when a valuable complete workflow
justifies its slower and more fragile execution. Once business behaviour, HTTP
translation, state-to-DOM rendering, DOM-to-intent forwarding and production
composition are covered, simple features should not expand their Angular test
matrix without a specific risk to protect.

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

## Verification

Build files and package scripts define the available tasks and their exact
composition. Use focused tests while developing, then run the repository-wide
gate before handoff. Production builds remain part of that gate because tests
alone do not prove that Spring packaging or Angular templates compile.

An up-to-date or cached task reused previous output rather than executing its
tests during that invocation. Formatting application and formatting
verification are separate operations; only the former should rewrite source.

Coverage percentages must not replace boundary-focused assertions. Introduce a
numeric threshold only through an explicit quality decision.

## Quality principles

- Protect business decisions and architectural boundaries.
- Test real infrastructure where its behaviour differs.
- Keep tests deterministic and failures understandable.
- Avoid coupling tests to private implementation details.
- Keep static checks, tests and production builds in the repository quality gate.
- Treat build files, package scripts and CI configuration as executable sources
  of truth.
