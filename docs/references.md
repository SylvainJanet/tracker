# References and sources of truth

## Purpose

This guide identifies which evidence to use for a project question. Sources have
different responsibilities; none describes the whole application.

## Choosing evidence

| Question                                    | Authoritative source                                   |
| ------------------------------------------- | ------------------------------------------------------ |
| What outcome is wanted?                     | Current explicit user direction                        |
| What does the application do now?           | Production code, tests and runtime configuration       |
| What is the schema history?                 | Flyway migrations, read in version order               |
| What commands and tools exist?              | Build files, wrappers, lockfiles and CI workflows      |
| What does a business concept mean?          | Relevant domain documentation                          |
| Which backend structural constraints apply? | Backend architecture tests                             |
| Why was an architectural choice made?       | Relevant architecture guide                            |
| What did the historical trackers establish? | Relevant domain documentation                          |
| How does a dependency behave?               | Official documentation matching the repository version |

Historical spreadsheets were used to derive the retained domain documentation
but are not part of the normal agent context. Use the corresponding domain
guides for historical tracker semantics rather than attempting to reconstruct
them from the source workbooks.

User direction defines the intended change; it does not redefine current
behaviour. Code and tests establish implemented behaviour but may lag documented
intent. When sources conflict, report the discrepancy rather than silently
choosing one. Change an accepted decision deliberately and update affected
documentation, code and tests.

## Internal documentation

Use the documentation map in [`AGENTS.md`](../AGENTS.md) to select the smallest
relevant set of guides. In particular:

- domain guides define intended business meaning, including concepts not yet
  implemented;
- backend architecture tests and the frontend ESLint architecture policy define
  enforceable restrictions; their current failures explain violations, while
  architecture guides retain rationale and non-static decisions;
- development guides own commands, testing and delivery workflow.

Search the codebase before relying on a documented path or type name. The source
tree may evolve without changing the underlying guidance.

## Repository and generated references

| Concern                       | Inspect                                                                                                                                                                                                                                       |
| ----------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Backend domain                | `backend/src/main/java/**/domain/` and corresponding unit tests                                                                                                                                                                               |
| Frontend domain and use cases | `frontend/src/app/` and focused specifications                                                                                                                                                                                                |
| Application behaviour         | Ports, commands, queries, services, results and application exceptions                                                                                                                                                                        |
| HTTP contract                 | Controllers, DTOs, exception handlers, integration tests, frontend HTTP adapters and generated OpenAPI                                                                                                                                        |
| Database schema               | Every file under `backend/src/main/resources/db/migration/`, in version order                                                                                                                                                                 |
| Build behaviour               | `gradlew`, root `build.gradle.kts`, `settings.gradle.kts`, `gradle/`, `backend/build.gradle.kts`, `frontend/build.gradle.kts`, `frontend/package.json`, `frontend/package-lock.json`, `frontend/angular.json` and `frontend/eslint.config.js` |
| Continuous integration        | `.github/workflows/` and the tasks it invokes                                                                                                                                                                                                 |

Generated evidence is useful but does not replace tests:

- Swagger UI reflects the compiled, running backend; restart it after changes.
- Gradle test reports are written under `backend/build/reports/tests/<task>/`.
- Integration-test SQLite files under `backend/build/test-databases/` are diagnostic
  artefacts, not maintained sources.
- Do not assume that frontend coverage output exists. Check `angular.json` and
  package scripts.

Do not commit generated reports, test databases or local application data.

## External technical documentation

Prefer official documentation. Before consulting it, determine the
repository-pinned version from:

- Java and Spring dependencies: `backend/build.gradle.kts`;
- Gradle: `gradle/wrapper/gradle-wrapper.properties`;
- Node.js: `frontend/.nvmrc`;
- Angular and TypeScript: `frontend/package.json` and `frontend/package-lock.json`.

An unversioned “current” manual may describe a newer release. Select compatible
documentation; do not change the application merely to match the latest guide.

### Java, Spring and API

- [Java 26 API](https://docs.oracle.com/en/java/javase/26/docs/api/)
- [Spring Boot 4.1 reference](https://docs.spring.io/spring-boot/4.1/reference/)
- [Spring MVC and `ProblemDetail`](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-ann-rest-exceptions.html)
- [Springdoc](https://springdoc.org/)
- [OpenAPI specification](https://spec.openapis.org/oas/latest.html) — select the version used by the generated API
  contract
- [RFC 9457: Problem Details for HTTP APIs](https://www.rfc-editor.org/rfc/rfc9457)

### Persistence

- [SQLite data types](https://www.sqlite.org/datatype3.html)
- [SQLite strict tables](https://www.sqlite.org/stricttables.html)
- [SQLite constraints](https://www.sqlite.org/lang_createtable.html)
- [Flyway](https://documentation.red-gate.com/flyway)

### Frontend, build and quality

- [Angular](https://angular.dev/) and [version compatibility](https://angular.dev/reference/versions)
- [TypeScript handbook](https://www.typescriptlang.org/docs/handbook/intro.html)
- [Gradle user manual](https://docs.gradle.org/current/userguide/) — select the wrapper version
- [Gradle JVM test suites](https://docs.gradle.org/current/userguide/jvm_test_suite_plugin.html)
- [Spotless](https://github.com/diffplug/spotless)
- [ArchUnit](https://www.archunit.org/userguide/html/000_Index.html)
- [ESLint](https://eslint.org/docs/latest/) and [Prettier](https://prettier.io/docs/)

## Maintenance

Update this file when the role or location of a source changes or a canonical
external reference becomes unsuitable. Record feature progress elsewhere.
