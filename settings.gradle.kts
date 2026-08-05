pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "Apiary_Mobile"
include(":app")
include(":navigation")
include(":auth")
include(":attendance")
include(":base")
include(":merchandise")
