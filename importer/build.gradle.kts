plugins {
    application
    java
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
    implementation("org.apache.commons:commons-csv:1.14.1")
}

application {
    mainClass = "fr.sylvainjanet.tracker.importer.TrackerImporterApplication"
}

tasks.named<JavaExec>("run") {
    workingDir(rootProject.projectDir)
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
        }
    }
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
    description = "Formats importer Java source files."
    dependsOn("spotlessApply")
}

tasks.named("check") {
    dependsOn("spotlessCheck")
}
