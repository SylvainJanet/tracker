# Development workflow

## Purpose

This guide explains how to set up, run, change and verify the repository safely.
Read the root `AGENTS.md` first, then load only the domain, architecture and
development documents relevant to the requested change.

Before editing, inspect the working tree, affected code and tests, and applicable
build configuration. Preserve unrelated user changes and do not infer paths or
implemented capabilities from documentation alone.

## Setup and data safety

Use the Gradle wrapper and committed frontend lockfile. Build files, wrappers
and lockfiles—not this guide—define tool versions, dependencies and tasks.

From a fresh checkout, create the repository-local `data/` directory if it does
not already exist, then verify the Gradle wrapper:

```bash
./gradlew --version
```

Frontend Gradle tasks invoke the node and npm executables from the current shell;
Gradle does not activate `frontend/.nvmrc`. The frontend task graph validates the
exact active Node.js version before installing dependencies, and `run.sh`
performs the same validation before starting the application. Before running
either entry point, use your Node.js version manager to activate the version
declared in `frontend/.nvmrc`.

For example, with nvm:

```bash
cd frontend
nvm install
nvm use
cd ..

node --version
./gradlew :frontend:install
```

If Gradle still observes the previous Node.js installation after switching
versions, stop its existing daemon with `./gradlew --stop` and rerun the task.

Keep the selected Node.js version active in the same shell when running
`./gradlew :frontend:*`, `./gradlew build`, or `./gradlew format`.
The `:frontend:install` task runs `npm ci`. Running `npm ci` directly from
`frontend/` is also valid; both entry points require the active Node.js version
to match `.nvmrc`.

Do not create SQLite database files manually; Flyway and the first application
connection create and initialise them.

The ignored `data/` directory contains the persistent personal database and may
contain sensitive health information. Never commit, log, delete or rewrite that
data without explicit user direction.

Development startup deliberately creates a local snapshot of
`data/tracker.db` under `backend/build/dev-database/`. This disposable snapshot
contains the same sensitive information and must not be committed, logged,
shared, copied into fixtures or used by automated tests. It may be deleted by
`clean` and is replaced on the next development startup.

Automated tests use only synthetic data in isolated databases under
`backend/build/test-databases/`.

Generated `backend/build/`, `frontend/dist/` and `frontend/node_modules/` content
is not committed. If legitimate source is unexpectedly ignored, run:

```bash
git check-ignore -v <path>
```

Inspect the responsible rule before adding a narrow exception; generic `out/`
rules must not hide source packages named `out`.

## Local database and debug modes

The combined development launcher supports three database modes:

- `local` recreates `backend/build/dev-database/tracker.db` from a consistent
  snapshot of `data/tracker.db`;
- `empty` removes the development database so Flyway can create a fresh schema;
- `real` connects directly to the persistent personal database at
  `data/tracker.db`.

Only use `--database=real` when changes to the personal database are intended.

In debug mode, the backend JVM listens on `localhost:5005` and remains suspended
until a remote JVM debugger attaches. The Angular development server starts
concurrently, but API requests cannot complete until the debugger is attached
and Spring Boot finishes starting.

The Angular development server proxies `/api/**` to `127.0.0.1:8080`. Backend
addresses belong in proxy or environment configuration, not in components or
gateways.

Frontend application code runs in the browser. The Angular development
configuration enables source maps, so the browser debugger must not start a
separate development server while run.sh is running.

Springdoc generates the OpenAPI description from the running application and
exposes Swagger UI for inspection. After an HTTP contract change, rebuild or
restart the backend and review the affected request, response, status, header
and error documentation. Generated output does not replace deliberate contract
design.

## Schema and API changes

Flyway migrations live under `backend/src/main/resources/db/migration` and use
names such as `V2__add_something.sql`. An unpublished migration may be corrected
only while it has not been applied to a persistent database or incorporated
into shared repository history. Once either is true, create a new migration.

Test schema changes on a fresh SQLite database using production migrations.
Verify constraints, repository mapping and upgrades of existing data when
relevant. Never alter the personal database merely to make a test pass.
Consider SQLite storage classes, `STRICT` behaviour, nullability, checks and
Java mapping.

An HTTP change may affect several contracts: transport DTOs and mappings,
application inputs or results, generated OpenAPI, and frontend HTTP
translation. Identify and update only the affected boundaries. Update the
corresponding backend controller and frontend gateway tests together when
both sides of the wire contract are affected.

Exact structural rules live in the executable backend and frontend architecture
policies linked from the [architecture overview](../architecture/overview.md).
Use the guides for rationale rather than as rule inventories.

## Dependencies

Add backend dependencies through Gradle and frontend dependencies through npm.
Prefer existing JDK, Spring, Angular, TypeScript or browser capabilities; choose
the narrowest configuration, distinguish runtime from development dependencies
and commit generated lockfile changes. Never edit `package-lock.json` manually.

Review compatibility and release notes for upgrades. Broad upgrades are separate
technical changes unless required by the requested feature, and dependency
changes receive the same full verification as production code.

## Git and documentation

Keep a change coherent: production behaviour, its tests, required migrations and
durable documentation may belong together. Avoid unrelated refactoring,
generated output, broad upgrades, IDE settings, personal data and machine-specific
paths. Do not discard or rewrite user work.

Documentation records durable meanings, decisions and public workflows—not
progress. Update the owning document when those change or when a durable
decision is superseded. Issues, pull requests and commit history carry
progress.
