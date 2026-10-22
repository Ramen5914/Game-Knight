pluginManagement {
    repositories {
        gradlePluginPortal()

        maven("https://maven.kikugie.dev/snapshots") {
            name = "KikuGie Snapshots"
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("dev.kikugie.stonecutter") version "0.10-beta.1"
}

stonecutter {
    centralScript = "build.gradle.kts"

    create(getRootProject()) {
        versions("26.2", "26.3")
    }
}
