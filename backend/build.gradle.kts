import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    java
    `java-test-fixtures`
    id("org.springframework.boot") version "4.1.1"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.diffplug.spotless")
}

group = "fr.sylvainjanet"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(26)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.1.0")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    runtimeOnly("org.xerial:sqlite-jdbc")
    testImplementation("org.springframework.boot:spring-boot-starter-jdbc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

spotless {
    java {
        googleJavaFormat("1.36.1").aosp()
        removeUnusedImports()
        forbidWildcardImports()
        forbidModuleImports()
        trimTrailingWhitespace()
        endWithNewline()
    }
}

tasks.register("format") {
    group = "formatting"
    description = "Formats Java source files."
    dependsOn(
        "spotlessApply",
    )
}

tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
    workingDir(rootProject.projectDir)
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
        }

        val architectureTest =
            register<JvmTestSuite>("architectureTest") {
                useJUnitJupiter()

                dependencies {
                    implementation(project())
                    implementation(
                        "com.tngtech.archunit:" +
                            "archunit-junit6:1.5.0",
                    )
                    implementation("org.springframework:spring-context")
                    implementation("org.springframework:spring-jdbc")
                    implementation("org.springframework:spring-web")
                    implementation("io.swagger.core.v3:swagger-annotations-jakarta:2.2.52")
                }

                targets {
                    all {
                        testTask.configure {
                            shouldRunAfter(test)
                        }
                    }
                }
            }

        register<JvmTestSuite>("integrationTest") {
            useJUnitJupiter()
            dependencies {
                implementation(project())
                implementation(testFixtures(project()))
                implementation(
                    "org.springframework.boot:spring-boot-starter-test",
                )
                implementation(
                    "org.springframework.boot:spring-boot-starter-jdbc-test",
                )
                implementation(
                    "org.springframework.boot:spring-boot-starter-webmvc-test",
                )
                implementation(
                    "org.springframework.boot:spring-boot-starter-flyway",
                )
                runtimeOnly("org.xerial:sqlite-jdbc")
            }
            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(
                            test,
                            architectureTest,
                        )
                    }
                }
            }
        }
    }
}

tasks.named("check") {
    dependsOn(
        "spotlessCheck",
        testing.suites.named("architectureTest"),
        testing.suites.named("integrationTest"),
    )
}

tasks.withType<Test>().configureEach {
    val currentTestTask = this
    addTestListener(
        object : TestListener {
            override fun beforeSuite(suite: TestDescriptor) = Unit

            override fun beforeTest(testDescriptor: TestDescriptor) = Unit

            override fun afterTest(
                testDescriptor: TestDescriptor,
                result: TestResult,
            ) = Unit

            override fun afterSuite(
                suite: TestDescriptor,
                result: TestResult,
            ) {
                if (suite.parent == null) {
                    currentTestTask.logger.lifecycle(
                        "> Task ${currentTestTask.name}" +
                            " - Passed ${result.successfulTestCount}" +
                            " - Ignored ${result.skippedTestCount}" +
                            " - Failed ${result.failedTestCount}",
                    )
                }
            }
        },
    )
}

val developmentDatabase =
    layout.buildDirectory.file("dev-database/tracker.db")
val personalDatabase =
    rootProject.layout.projectDirectory.file("data/tracker.db")

val databasePreparerMainClass =
    "fr.sylvainjanet.tracker.configuration.sqlite.DevelopmentDatabasePreparer"
val applicationMainClass =
    "fr.sylvainjanet.tracker.TrackerApplication"

val prepareDevelopmentDatabase =
    tasks.register<JavaExec>("prepareDevelopmentDatabase") {
        group = "application"
        description =
            "Recreates the development database from a snapshot of the personal database."

        dependsOn(tasks.named("classes"))

        classpath = sourceSets.main.get().runtimeClasspath
        mainClass.set(databasePreparerMainClass)
        args(
            "snapshot",
            personalDatabase.asFile.absolutePath,
            developmentDatabase.get().asFile.absolutePath,
        )
    }

val prepareEmptyDevelopmentDatabase =
    tasks.register<JavaExec>("prepareEmptyDevelopmentDatabase") {
        group = "application"
        description =
            "Removes the disposable development database."

        dependsOn(tasks.named("classes"))

        classpath = sourceSets.main.get().runtimeClasspath
        mainClass.set(databasePreparerMainClass)
        args(
            "empty",
            developmentDatabase.get().asFile.absolutePath,
        )
    }

val bootRun =
    tasks.named<BootRun>("bootRun") {
        group = "application"
        description =
            "Starts the backend with a fresh snapshot of the personal database."

        dependsOn(prepareDevelopmentDatabase)

        workingDir(rootProject.projectDir)
        systemProperty(
            "spring.datasource.url",
            "jdbc:sqlite:${developmentDatabase.get().asFile.absolutePath}",
        )
    }

val bootRunEmpty =
    tasks.register<BootRun>("bootRunEmpty") {
        group = "application"
        description =
            "Starts the backend with a fresh empty development database."

        dependsOn(prepareEmptyDevelopmentDatabase)

        classpath = sourceSets.main.get().runtimeClasspath
        mainClass.set(applicationMainClass)
        workingDir(rootProject.projectDir)
        systemProperty(
            "spring.datasource.url",
            "jdbc:sqlite:${developmentDatabase.get().asFile.absolutePath}",
        )
    }

val bootRunReal =
    tasks.register<BootRun>("bootRunReal") {
        group = "application"
        description =
            "Starts the backend with the persistent personal database."

        classpath = sourceSets.main.get().runtimeClasspath
        mainClass.set(applicationMainClass)
        workingDir(rootProject.projectDir)
        systemProperty(
            "spring.datasource.url",
            "jdbc:sqlite:${personalDatabase.asFile.absolutePath}",
        )
    }
