plugins {
    base
    id("com.diffplug.spotless")
}

repositories {
    mavenCentral()
}

spotless {
    format("markdown") {
        target(
            "*.md",
            "**/*.md",
        )
        targetExclude("**/node_modules/**")
        prettier("3.9.6").configFile("prettier.config.mjs")
    }
    kotlinGradle {
        target(
            "*.gradle.kts",
            "backend/*.gradle.kts",
            "frontend/*.gradle.kts",
        )
        ktlint()
        trimTrailingWhitespace()
        endWithNewline()
    }
}

tasks.register("format") {
    group = "formatting"
    description = "Formats the complete repository."

    dependsOn(
        "spotlessApply",
        ":backend:format",
        ":frontend:format",
    )
}

tasks.named("check") {
    description = "Verifies the complete repository."

    dependsOn(
        "spotlessCheck",
        ":backend:check",
        ":frontend:check",
    )
}

tasks.named("build") {
    description = "Builds and verifies the complete repository."

    dependsOn(
        ":backend:build",
        ":frontend:build",
    )
}
