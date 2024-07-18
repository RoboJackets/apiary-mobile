buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
        classpath("com.android.tools.build:gradle:8.5.1")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.51.1") // This version needs to
        // match the version for other Hilt dependencies defined in Dependencies.kt
        classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")
        classpath("com.google.gms:google-services:4.4.2")
        classpath("com.google.android.gms:oss-licenses-plugin:0.10.6")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
    configurations {
        all {
            exclude("com.google.android.gms", "play-services-ads-identifier")
            exclude("com.google.android.gms", "play-services-measurement")
            exclude("com.google.android.gms", "play-services-measurement-sdk")
        }
    }
}

plugins {
    id("com.google.devtools.ksp").version("1.9.0-1.0.13").apply(false)
    id("io.gitlab.arturbosch.detekt").version("1.23.0")
    id("com.autonomousapps.dependency-analysis").version("1.21.0")
    id("com.github.ben-manes.versions").version("0.46.0")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}

val projectSource = file(projectDir)
val configFile = files("$rootDir/ci/detekt/detekt.yml")
val baselineFile = file("$rootDir/ci/detekt/baseline.xml")
val kotlinFiles = "**/*.kt"
val resourceFiles = "**/resources/**"
val buildFiles = "**/build/**"

tasks.register("detektAll", io.gitlab.arturbosch.detekt.Detekt::class) {
    val autoFix = project.hasProperty("detektAutoFix")

    description = "Custom DETEKT build for all modules"
    parallel = true
    ignoreFailures = false
    autoCorrect = autoFix
    buildUponDefaultConfig = true
    setSource(projectSource)
    config.setFrom(configFile)
    include(kotlinFiles)
    exclude(resourceFiles, buildFiles)
    reports {
        html.required.set(true)
        xml.required.set(false)
        txt.required.set(false)
    }
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.0")
}

// from https://github.com/autonomousapps/dependency-analysis-android-gradle-plugin/wiki/ABI-filtering
dependencyAnalysis {
    abi {
        exclusions {
            // Convenience helpers
            ignoreSubPackage("internal")
            ignoreInternalPackages()
            ignoreGeneratedCode()

            // Raw, regexp-based APIs
            excludeAnnotations(".*\\.Generated")
            excludeClasses(".*\\.internal\\..*")
        }
    }
}