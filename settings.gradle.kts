pluginManagement {
    repositories {
        google()
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
        google()
        mavenCentral()
        maven("https://www.jitpack.io")
        maven("https://maven.aliyun.com/repository/public/")
        maven("https://maven.aliyun.com/repository/central")
        maven ( "https://oss.sonatype.org/content/repositories/snapshots/" )
    }
}
rootProject.name = "Weather"
include(":app")
include(":icon")
include(":libx")
include(":compose_lib")

// Your relative path or absolute path
includeBuild("pullrefresh") {
    dependencySubstitution {
        substitute(module("me.omico.lux:lux-androidx-compose-material3-pullrefresh")).using(project(":"))
    }
}