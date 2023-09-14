pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://www.jitpack.io")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://www.jitpack.io")
        maven ( "https://oss.sonatype.org/content/repositories/snapshots/" )
    }
}
rootProject.name = "Weather"
include(":app")
include(":icon")
include(":libx")
include(":compose_lib")
