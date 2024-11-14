pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.aliyun.com/repository/public/")
        maven("https://maven.aliyun.com/repository/central")
        maven("https://www.jitpack.io")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven("https://www.jitpack.io")
        maven("https://maven.aliyun.com/repository/public/")
        maven("https://maven.aliyun.com/repository/central")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
    }
    versionCatalogs {
        create("buildLibs") {
            from(files("./build-logic/gradle/libs.versions.toml"))
        }
        create("composeLibs") {
            from(files("./build-logic/gradle/composeLibs.versions.toml"))
        }
        create("others") {
            from(files("./build-logic/gradle/others.versions.toml"))
        }
    }
}

rootProject.name = "Weather"
include(":app")
include(":icon")
include(":libx")
include(":compose_lib")