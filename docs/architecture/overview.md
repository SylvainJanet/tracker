# Architecture overview

## Authority and use

Production code and behavioural tests define implemented behaviour. For exact
package, file, naming, declaration and dependency restrictions, use these sources
in order:

1. the executable architecture policy and its contract tests;
2. the diagnostic produced by the current failing architecture check;
3. this documentation.

If they disagree, the documentation is stale. The guides record intent and
decisions that static checks cannot express; they are not rule inventories.

| Need                                          | Backend authority                                                                                                                                          | Frontend authority                                                                                                                  |
| --------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------- |
| Structure and conventions                     | [`StructuralConventionTest`](../../backend/src/architectureTest/java/fr/sylvainjanet/tracker/architecture/tests/StructuralConventionTest.java)             | [`architecture.js`](../../frontend/eslint/architecture/architecture.js) and its [`rules`](../../frontend/eslint/architecture/rules) |
| Dependency direction and technical boundaries | [`HexagonalArchitectureTest`](../../backend/src/architectureTest/java/fr/sylvainjanet/tracker/architecture/tests/HexagonalArchitectureTest.java)           | [`architecture.js`](../../frontend/eslint/architecture/architecture.js) and [`eslint.config.js`](../../frontend/eslint.config.js)   |
| Context and module access                     | [`BoundedContextArchitectureTest`](../../backend/src/architectureTest/java/fr/sylvainjanet/tracker/architecture/tests/BoundedContextArchitectureTest.java) | [`module-boundaries.js`](../../frontend/eslint/architecture/rules/module-boundaries.js)                                             |
| Policy contract coverage                      | [`architecture/contract`](../../backend/src/architectureTest/java/fr/sylvainjanet/tracker/architecture/contract)                                           | [`architecture/test`](../../frontend/eslint/architecture/test)                                                                      |

Run backend policy with `./gradlew :backend:architectureTest`. Frontend production
source is checked by `./gradlew :frontend:lint`; `./gradlew
:frontend:architectureTest` verifies the custom policy implementation. The
frontend `check` task runs both.

## Shared intent

The application uses domain-driven design and hexagonal architecture to keep
business behaviour independent from technical boundaries. Code is organised
first around bounded contexts. A cohesive feature or slice may subdivide a
context without becoming a context of its own, and backend and frontend
structures need not mirror one another.

Context boundaries follow business meaning and ownership. Spreadsheet sheets,
routes, pages, database tables and CRUD operations are evidence, not boundary
definitions. Domain-modelling principles are recorded in
[domain modelling decisions](../domain/modelling-decisions.md).

## Responsibilities

The domain owns business meaning, state and invariants. The application owns
use-case coordination and its boundary contracts. A driving adapter translates
an external interaction into an application call; a driven adapter implements a
capability required by the application. Configuration composes implementations
without owning business behaviour.

“Inbound” and “outbound” are relative to the application. The application calls
a driven adapter through an outbound abstraction that the application owns. This
ownership, rather than runtime call direction, explains why dependencies point
inward.

Application services follow use-case cohesion rather than a mechanical
one-service-per-operation rule. Commands request state changes and queries request
information without intending to change business state; this distinction does not
require full CQRS or duplicated models.

## Boundaries and collaboration

Mapping belongs to the boundary whose representation it translates. Errors
originate where they can be understood and are translated outwards; domain and
application code do not choose transport statuses or presentation formatting. A
backend persistence adapter may reconstitute its application's aggregate, while a
frontend gateway must translate backend-controlled data into frontend-owned
contracts.

Cross-context interaction requires deliberate contract ownership. Direct calls
suit workflows that require an immediate result, ordering or failure. Events suit
meaningful facts whose consumers should remain independent; they describe what
happened rather than disguise commands. Presentation may compose contexts without
merging their models or ownership.

Generic technical utilities may be shared without becoming a domain shared
kernel. A shared kernel is reserved for a small, explicitly governed concept with
the same meaning for all participants.

## Restraint

Hexagonal architecture does not require an interface for every class, a mapper
for every field copy, an event for every change, a DTO at every method call or a
context for every screen. Introduce an abstraction when it protects meaningful
ownership, dependency or variation.
