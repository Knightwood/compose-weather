package com.kiylx.common.logic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    isLibrary:Boolean,
) {
    commonExtension.apply {
        buildFeatures {
            compose = true
        }
        packaging {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
        }
        testOptions {
            unitTests {
                // For Robolectric
                isIncludeAndroidResources = true
            }
        }
    }
    //依赖
    configComposeModuleDeps()
    extensions.configure<ComposeCompilerGradlePluginExtension> {
        fun Provider<String>.onlyIfTrue() = flatMap { provider { it.takeIf(String::toBoolean) } }
        fun Provider<*>.relativeToRootProject(dir: String) = flatMap {
            rootProject.layout.buildDirectory.dir(projectDir.toRelativeString(rootDir))
        }.map { it.dir(dir) }

        project.providers.gradleProperty("enableComposeCompilerMetrics").onlyIfTrue()
            .relativeToRootProject("compose-metrics")
            .let(metricsDestination::set)

        project.providers.gradleProperty("enableComposeCompilerReports").onlyIfTrue()
            .relativeToRootProject("compose-reports")
            .let(reportsDestination::set)

        //这个从project目录下读取compose_compiler_config.conf文件，
        //然后将配置文件中的类或包下的所有类，视为compose中的immutable classes
        stabilityConfigurationFile =
            rootProject.layout.projectDirectory.file("compose_compiler_config.conf")

        enableStrongSkippingMode = true
    }
}

internal fun Project.configComposeModuleDeps() {
    dependencies {
        val composeBomVersion = composeLibs2.findVersion("bom").get()
        val bom = composeBomVersion.requiredVersion
//    print(
//        "strictVersion:" + composeBomVersion.strictVersion + "\n" +
//                "requiredVersion:" + composeBomVersion.requiredVersion + "\n" +
//                "preferredVersion:" + composeBomVersion.preferredVersion + "\n" +
//                "rejectedVersions:" + composeBomVersion.rejectedVersions + "\n" +
//                "branch:" + composeBomVersion.branch + "\n" +
//                "displayName" + composeBomVersion.displayName + "\n"
//    )

        val composeBom = platform("androidx.compose:compose-bom:${bom}")
        implementationDeps(composeBom)
        androidTestImplementationDeps(composeBom)

        // Choose one of the following:
        // Material Design 3
        implementationDeps(composeLibs2.libFind("androidx-material3-compose"))
//    implementationDeps("androidx.compose.material3:material3")
        // or Material Design 2
//          implementation("androidx.compose.material:material")
        // or skip Material Design and build directly on top of foundational components
//          implementation("androidx.compose.foundation:foundation")
        // or only import the main APIs for the underlying toolkit systems,
        // such as input and measurement/layout
//          implementation("androidx.compose.ui:ui")

        // Android Studio Preview support
        implementationDeps("androidx.compose.ui:ui-tooling-preview")
        debugImplementationDeps("androidx.compose.ui:ui-tooling")

        // UI Tests
        androidTestImplementationDeps("androidx.compose.ui:ui-test-junit4")
        debugImplementationDeps("androidx.compose.ui:ui-test-manifest")

        // Optional - Included automatically by material, only add when you need
        // the icons but not the material library (e.g. when using Material3 or a
        // custom design system based on Foundation)
//    implementationDeps("androidx.compose.material:material-icons-core")
        implementationDeps("androidx.compose.material:material-icons-extended")
        // Optional - Add full set of material icons
//          implementation("androidx.compose.material:material-icons-extended")
        // Optional - Add window size utils
        implementationDeps("androidx.compose.material3:material3-window-size-class")
        // Optional - Integration with activities
        implementationDeps(composeLibs2.libFind("androidx-activity-compose"))
        // Optional - Integration with ViewModels
        implementationDeps(composeLibs2.libFind("androidx-lifecycle-viewmodel-compose"))
        // Optional - Integration with LiveData
        implementationDeps("androidx.compose.runtime:runtime-livedata")

        //test
        androidTestImplementationDeps(platform("androidx.compose:compose-bom:${bom}"))

        // kotlinx 提供的 immutable 集合工具类
        implementationDeps("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.7")
    }
}
