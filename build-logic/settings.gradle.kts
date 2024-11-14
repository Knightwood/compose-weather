dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    //将composeLibs.versions.toml提供给内部使用
    versionCatalogs {
        create("composeLibs") {
            from(files("./gradle/composeLibs.versions.toml"))
        }
    }
}

rootProject.name = "build-logic"
include(":convention")
