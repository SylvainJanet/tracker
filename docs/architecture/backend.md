# Backend architecture

## Authority

The shared intent is in the [architecture overview](overview.md). Exact backend
structure and dependency restrictions are executable in the architecture tests;
the overview links each concern to its owning policy. Run them with `./gradlew
:backend:architectureTest` and use the failure before consulting this guide.

Build files define the technology set and versions. Production code and tests
define implemented behaviour, while Flyway migrations define schema history.

## Decisions outside static policy

HTTP resource identity follows domain identity rather than introducing a
technical identifier without a demonstrated need. Focused application and HTTP
tests define the resulting behaviour.

Boundary failures are translated into useful protocol responses without exposing
stack traces, SQL or sensitive data.

Persistence representations preserve domain distinctions rather than silently
substituting convenient technical defaults. Migrations and SQLite integration
tests are authoritative for the concrete schema, representations and constraints.

Operational database, migration and verification procedures belong in the
[workflow](../development/workflow.md) and
[testing guide](../development/testing-and-quality.md), not in this architecture
guide.
