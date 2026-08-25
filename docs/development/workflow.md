# Development workflow

## Purpose

This guide explains how to set up, run, change and verify the repository safely.
Read the root `AGENTS.md` first, then load only the domain, architecture and
development documents relevant to the requested change.

Before editing, inspect the working tree, affected code and tests, and applicable
build configuration. Preserve unrelated user changes and do not infer paths or
implemented capabilities from documentation alone.

## Setup and data safety

Use the Gradle wrapper for backend and repository tasks. It pins Gradle, while
the build defines the Java toolchain. Root `settings.gradle.kts` and
`build.gradle.kts` define project membership and repository-wide orchestration;
`backend/build.gradle.kts` owns backend plugins, dependencies and tasks;
`frontend/build.gradle.kts` owns frontend Gradle task orchestration.
Frontend dependencies are governed by
`frontend/package.json`, `frontend/package-lock.json` and `frontend/.nvmrc`.
Use the committed lockfile rather than an unconstrained dependency update.

From a fresh checkout, create the repository-local `data/` directory if it does
not already exist, then verify the Gradle wrapper:

```bash
./gradlew --version
```

Frontend Gradle tasks invoke the node and npm executables from the current shell;
Gradle does not activate `frontend/.nvmrc`. Before running a frontend task or a
repository-wide build, use your Node.js version manager to activate the version
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

## Run locally

Use `run.sh` to start the backend and frontend together. By default, it runs in
normal mode with a disposable snapshot of the personal database:

```bash
./run.sh
```

Select the database with `--database`:

```bash
./run.sh --database=local
./run.sh --database=empty
./run.sh --database=real
```

The database modes are:

- `local` recreates `backend/build/dev-database/tracker.db` from a consistent
  snapshot of `data/tracker.db`;
- `empty` removes the development database so Flyway can create a fresh schema;
- `real` connects directly to the persistent personal database at
  `data/tracker.db`.

Only use `--database=real` when changes to the personal database are intended.

Enable backend debugging with `--mode=debug`:

```bash
./run.sh --mode=debug
```

The mode and database options can be combined:

```bash
./run.sh --mode=debug --database=empty
./run.sh --mode=debug --database=real
```

In debug mode, the backend JVM listens on `localhost:5005` and remains suspended
until a remote JVM debugger attaches. The Angular development server starts
concurrently, but API requests cannot complete until the debugger is attached
and Spring Boot finishes starting.

The application services are available at:

| Service             | Address                        |
| ------------------- | ------------------------------ |
| Angular application | localhost:4200                 |
| Spring Boot API     | localhost:8080                 |
| Swagger UI          | localhost:8080/swagger-ui.html |

The Angular development server proxies `/api/**` to `127.0.0.1:8080`. Backend
addresses belong in proxy or environment configuration, not in components or
gateways.

Configure the debuggers as follows:

| Application | Debugger configuration                               |
| ----------- | ---------------------------------------------------- |
| Backend     | Remote JVM debugger attached to localhost:5005       |
| Frontend    | Browser JavaScript debugger opened at localhost:4200 |

Frontend application code runs in the browser. The Angular development
configuration enables source maps, so the browser debugger must not start a
separate development server while run.sh is running.

The backend and frontend can be started independently when troubleshooting:

```bash
./gradlew :backend:bootRun
./gradlew :backend:bootRunEmpty
./gradlew :backend:bootRunReal
./gradlew :frontend:start
```

Append `--debug-jvm` to a backend launch command to wait for a debugger:

```bash
./gradlew :backend:bootRun --debug-jvm
```

Stop a combined local run with `Ctrl+C`.

Springdoc generates the OpenAPI description from the running application and
exposes Swagger UI for inspection. After an HTTP contract change, rebuild or
restart the backend and review the affected request, response, status, header
and error documentation. Generated output does not replace deliberate contract
design.

## Everyday change sequence

1. Identify the owning feature, use case, domain meaning and affected public,
   persistence or HTTP contracts.
2. Locate the innermost layer that can own the decision; translate it at adapter
   boundaries rather than putting business rules in controllers or components.
3. Add or update the lowest-level test that reliably demonstrates the change. A
   defect fix normally includes a regression test.
4. Implement without bypassing ports, leaking transport types inward or mixing
   unrelated refactoring.
5. Run focused tests, apply the appropriate formatter and inspect its changes.
6. Run `./gradlew build` before considering the repository complete.
7. Review `git status` and `git diff` for generated data, unrelated formatting,
   debug output, temporary configuration, personal paths and missing tests.

[Testing and quality](testing-and-quality.md) maps change types to suites and
explains database isolation, reports and test discovery.

## Command reference

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
| Verify frontend                             | `./gradlew :frontend:check`              |
| Build and verify complete repository        | `./gradlew build`                        |
| Format complete repository                  | `./gradlew format`                       |

Use `./gradlew tasks` and `npm run` to discover the executable task graph and
scripts. Filter a Java suite during development with the corresponding Gradle
task and `--tests`; a filtered run is not complete verification. Use
`--rerun-tasks` only when an actual rerun is needed.

Formatting commands modify files. `:backend:check`, `:frontend:check` and `check`
verify formatting without rewriting maintained source.

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

## Dependencies and CI

Add backend dependencies through Gradle and frontend dependencies through npm.
Prefer existing JDK, Spring, Angular, TypeScript or browser capabilities; choose
the narrowest configuration, distinguish runtime from development dependencies
and commit generated lockfile changes. Never edit `package-lock.json` manually.

Review compatibility and release notes for upgrades. Broad upgrades are separate
technical changes unless required by the requested feature, and dependency
changes receive the same full verification as production code.

CI activates the pinned version through `actions/setup-node` before invoking Gradle.

CI runs `./gradlew build --no-daemon` for pushes and pull requests targeting
`master`. It uses the Gradle wrapper, `frontend/.nvmrc`, the committed npm
lockfile and isolated test databases. Keep CI aligned with the local command,
permissions minimal and clean execution independent of caches. Branch protection
is configured on the hosting platform, not by the workflow alone.

## Git and documentation

Keep a change coherent: production behaviour, its tests, required migrations and
durable documentation may belong together. Avoid unrelated refactoring,
generated output, broad upgrades, IDE settings, personal data and machine-specific
paths. Do not discard or rewrite user work.

Documentation records durable meanings, decisions and public workflows—not
progress. Update the owning document when those change or when a durable
decision is superseded. Issues, pull requests and commit history carry
progress.

## Troubleshooting

| Symptom                           | Check                                                                                            |
| --------------------------------- | ------------------------------------------------------------------------------------------------ |
| SQLite file is absent             | Confirm `data/` exists, the backend connected and Flyway ran from the expected working directory |
| Source file is ignored            | Run `git check-ignore -v <path>` and inspect `out/` rules before adding an exception             |
| Gradle shows no test details      | Check task outcome, summary and reports; use `--rerun-tasks` only to force execution             |
| Frontend cannot reach the backend | Confirm backend port `8080` and the committed proxy target                                       |

Do not silence a lint rule or change committed runtime configuration before
understanding which boundary or environment assumption is wrong.

## Definition of done

- The behaviour is implemented at its owning boundary.
- Relevant focused, integration and architecture tests pass.
- Formatting and frontend lint pass.
- Backend and frontend production builds pass.
- `./gradlew build` succeeds.
- OpenAPI/Swagger UI is reviewed for HTTP changes and migrations for
  persistence changes.
- No generated, private or unrelated files are included.
- The final diff contains only intended changes.
- Durable documentation and decisions are current.
