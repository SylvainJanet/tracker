pluginManagement {
    plugins {
        id("com.diffplug.spotless") version "8.10.0"
    }
}

rootProject.name = "tracker"

include("backend")
include("frontend")
