pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "Apiary_Mobile"
include(":app")
include(":navigation")
include(":auth")
include(":attendance")
include(":base")
