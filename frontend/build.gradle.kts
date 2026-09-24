import java.io.ByteArrayOutputStream

abstract class ValidateNodeVersionTask : DefaultTask() {
  @get:InputFile
  abstract val nodeVersionFile: RegularFileProperty

  @get:Inject
  abstract val execOperations: ExecOperations

  @TaskAction
  fun validateNodeVersion() {
    val expectedVersion =
      nodeVersionFile
        .get()
        .asFile
        .readText()
        .trim()
    val nodeVersionOutput = ByteArrayOutputStream()

    val actualVersion =
      try {
        execOperations.exec {
          commandLine("node", "--version")
          standardOutput = nodeVersionOutput
        }

        nodeVersionOutput
          .toString(Charsets.UTF_8)
          .trim()
          .removePrefix("v")
      } catch (exception: GradleException) {
        throw GradleException(
          """
          Node.js $expectedVersion is required, but `node --version` could not be executed.
          Activate the required version before running frontend Gradle tasks.
          With nvm, run from the repository root:
            cd frontend && nvm install && nvm use && cd ..
          Then restart the Gradle daemon so it inherits the updated PATH:
            ./gradlew --stop
          """.trimIndent(),
          exception,
        )
      }
    if (actualVersion != expectedVersion) {
      throw GradleException(
        """
        Node.js $expectedVersion is required, but Node.js $actualVersion is active.
        Activate the required version before running frontend Gradle tasks.
        With nvm, run from the repository root:
          cd frontend && nvm install && nvm use && cd ..
        Then restart the Gradle daemon so it inherits the updated PATH:
          ./gradlew --stop
        """.trimIndent(),
      )
    }
  }
}

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

val validateNodeVersion =
  tasks.register<ValidateNodeVersionTask>("validateNodeVersion") {
    group = "verification"
    description = "Validates the active Node.js version."

    nodeVersionFile.set(
      layout.projectDirectory.file(".nvmrc"),
    )
  }

val install =
  tasks.register<Exec>("install") {
    group = "frontend"
    description =
      "Installs the frontend dependencies."

    dependsOn(validateNodeVersion)

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
