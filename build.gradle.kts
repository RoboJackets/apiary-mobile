buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.21")
        classpath("com.android.tools.build:gradle:7.2.1")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.40.1")
        classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")
        classpath("com.google.gms:google-services:4.3.10")
        classpath("com.google.android.gms:oss-licenses-plugin:0.10.5")
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
    id("io.gitlab.arturbosch.detekt").version("1.20.0")
    id("com.autonomousapps.dependency-analysis").version("1.4.0")
    id("com.github.ben-manes.versions").version("0.42.0")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

val projectSource = file(projectDir)
val configFile = files("$rootDir/config/detekt/detekt.yml")
val baselineFile = file("$rootDir/config/detekt/baseline.xml")
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
        html.enabled = true
        xml.enabled = false
        txt.enabled = false
    }
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.16.0")
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