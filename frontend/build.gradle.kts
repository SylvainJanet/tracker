import org.gradle.api.tasks.Exec

val npmExecutable =
  if (
    System
      .getProperty("os.name")
      .lowercase()
      .contains("windows")
  ) {
    "npm.cmd"
  } else {
    "npm"
  }

val install =
  tasks.register<Exec>("install") {
    group = "frontend"
    description =
      "Installs the frontend dependencies."

    workingDir(layout.projectDirectory.asFile)

    commandLine(
      npmExecutable,
      "ci",
    )

    inputs.files(
      layout.projectDirectory.file("package.json"),
      layout.projectDirectory.file("package-lock.json"),
    )

    outputs.file(
      layout.projectDirectory.file(
        "node_modules/.package-lock.json",
      ),
    )
  }

val build =
  tasks.register<Exec>("build") {
    group = "build"
    description =
      "Creates the frontend production build."

    dependsOn(install, check)

    workingDir(layout.projectDirectory.asFile)

    commandLine(
      npmExecutable,
      "run",
      "build",
    )
  }

val formatOnly =
  tasks.register<Exec>("formatOnly") {
    group = "formatting"
    description =
      "Formats frontend source files."

    dependsOn(install)

    workingDir(layout.projectDirectory.asFile)

    commandLine(
      npmExecutable,
      "run",
      "format",
    )
  }

val formatOnlyCheck =
  tasks.register<Exec>("formatOnlyCheck") {
    group = "verification"
    description =
      "Checks frontend source formatting."

    dependsOn(install)

    workingDir(layout.projectDirectory.asFile)

    commandLine(
      npmExecutable,
      "run",
      "format:check",
    )
  }

val lint =
  tasks.register<Exec>("lint") {
    group = "verification"
    description =
      "Lints frontend."

    dependsOn(install)
    mustRunAfter(formatOnly)

    workingDir(layout.projectDirectory.asFile)

    commandLine(
      npmExecutable,
      "run",
      "lint",
    )
  }

val architectureTest =
  tasks.register<Exec>("architectureTest") {
    group = "verification"
    description =
      "Runs frontend architecture tests."

    dependsOn(install)
    mustRunAfter(lint)

    workingDir(layout.projectDirectory.asFile)

    commandLine(
      npmExecutable,
      "run",
      "test:architecture",
    )
  }

val architectureTypeCheck =
  tasks.register<Exec>("architectureTypeCheck") {
    group = "verification"
    description =
      "Type-checks the frontend architecture rules."

    dependsOn(install)
    mustRunAfter(lint)

    workingDir(layout.projectDirectory.asFile)

    commandLine(
      npmExecutable,
      "run",
      "typecheck:architecture",
    )
  }

architectureTest {
  dependsOn(architectureTypeCheck)
  mustRunAfter(architectureTypeCheck)
}

val test =
  tasks.register<Exec>("test") {
    group = "verification"
    description =
      "Runs frontend unit tests."

    dependsOn(
      install,
      architectureTest,
    )
    mustRunAfter(lint)

    workingDir(layout.projectDirectory.asFile)

    commandLine(
      npmExecutable,
      "run",
      "test",
      "--",
      "--watch=false",
    )
  }

val format =
  tasks.register("format") {
    group = "formatting"
    description =
      "Formats and lints frontend source files."

    dependsOn(
      formatOnly,
      lint,
    )
  }

val check =
  tasks.register("check") {
    group = "verification"
    description =
      "Checks formatting, lints, and tests the frontend."

    dependsOn(
      formatOnlyCheck,
      lint,
      test,
    )
  }

val start =
  tasks.register<Exec>("start") {
    group = "frontend"
    description =
      "Starts the frontend development server."

    dependsOn(install)

    workingDir(layout.projectDirectory.asFile)

    commandLine(
      npmExecutable,
      "run",
      "start",
    )
  }

tasks.register<Delete>("clean") {
  group = "frontend"
  description =
    "Cleans the frontend build directory."

  delete(layout.projectDirectory.dir("dist"))
}
