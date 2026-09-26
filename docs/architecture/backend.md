# Backend architecture

## Authority

The [architecture overview](overview.md) records shared intent. Executable
architecture policy defines exact backend structure and dependencies; this
guide records decisions that policy cannot explain.

## Statistics module

Statistics is a supporting mathematical module rather than a bounded context. It
follows the same hexagonal dependency direction as bounded-context modules; its
lack of business vocabulary does not justify a separate architecture policy.

As mathematical behaviour is introduced, its domain owns indexed numeric
series, windows, calculation invariants and reusable statistical operations.
Its application layer owns the inbound calculation contracts. Consumers access
Statistics only through those contracts.

Driving adapters, driven adapters and outbound ports are introduced only for a
concrete external interaction or dependency. In-process consumers otherwise use
the inbound application contracts directly.

Statistics contracts contain numbers, indexes, windows and mathematical results.
They do not contain calendar dates, weights, calories, nutrients, journal
completion, strategy policies or presentation concepts.

Missing indexes remain observable as gaps and are not replaced with numeric
zero. Statistics does not decide value eligibility, gap-resolution policies,
units, provenance or calculation-method selection.

Analysis translates context-owned inputs into Statistics commands and translates
Statistics results back into meaningful dated results. Other bounded contexts
may reuse Statistics without sharing their domain models or analytical meanings.

The existing structural, hexagonal and module-boundary policies apply to
Statistics without a special architectural exception. Its domain and
application code remain framework-free.

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
