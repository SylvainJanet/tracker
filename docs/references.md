# References and sources of truth

## Purpose

The source-authority order is maintained in the root `AGENTS.md`. This guide
curates external references for questions the repository cannot answer by
itself; it does not duplicate source paths, tool versions or generated-output
locations.

Historical spreadsheets were used to derive the retained tracker-context
documentation but are not part of normal agent context. Use those guides for
legacy semantics rather than reconstructing them from personal workbooks.

## Selecting technical documentation

Prefer official documentation and first determine the relevant version from the
repository's build files, wrappers and lockfiles. An unversioned current manual
may describe a newer release; select compatible documentation rather than
changing the application to match the latest guide.

### Java, Spring and API

- [Java API documentation](https://docs.oracle.com/en/java/javase/)
- [Spring Boot reference](https://docs.spring.io/spring-boot/index.html)
- [Spring MVC and `ProblemDetail`](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-ann-rest-exceptions.html)
- [Springdoc](https://springdoc.org/)
- [OpenAPI specification](https://spec.openapis.org/oas/latest.html)
- [RFC 9457: Problem Details for HTTP APIs](https://www.rfc-editor.org/rfc/rfc9457)

### Persistence

- [SQLite data types](https://www.sqlite.org/datatype3.html)
- [SQLite strict tables](https://www.sqlite.org/stricttables.html)
- [SQLite constraints](https://www.sqlite.org/lang_createtable.html)
- [Flyway](https://documentation.red-gate.com/flyway)

### Frontend, build and quality

- [Angular](https://angular.dev/) and [version compatibility](https://angular.dev/reference/versions)
- [TypeScript handbook](https://www.typescriptlang.org/docs/handbook/intro.html)
- [Gradle user manual](https://docs.gradle.org/current/userguide/)
- [Gradle JVM test suites](https://docs.gradle.org/current/userguide/jvm_test_suite_plugin.html)
- [Spotless](https://github.com/diffplug/spotless)
- [ArchUnit](https://www.archunit.org/userguide/html/000_Index.html)
- [ESLint](https://eslint.org/docs/latest/) and [Prettier](https://prettier.io/docs/)

## Maintenance

Keep only references that help explain or verify a project decision. Versions
remain owned by executable repository configuration.
