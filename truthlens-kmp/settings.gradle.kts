pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    plugins {
        kotlin("multiplatform") version "1.9.22"
        id("org.jetbrains.compose") version "1.6.0"
        id("com.android.application") version "8.2.2"
        id("com.android.library") version "8.2.2"
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

rootProject.name = "truthlens-kmp"
include(":composeApp")