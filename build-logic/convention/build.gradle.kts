import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
    //`java-gradle-plugin`
}

group = "com.kiylx.common.build_logic"

// Configure the build-logic plugins to target JDK 17
// This matches the JDK used to build the project, and is not related to what is running on device.
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    implementation("androidx.room:room-gradle-plugin:2.6.1")
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.android.tools.common)
    //Make version catalog more type safe
//    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
//    implementation(files(composeLibs.javaClass.superclass.protectionDomain.codeSource.location))
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {

    plugins {
        register("androidApplication") {
            id = "kiylx.build_logic.android.application"
            implementationClass = "plugin.AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "kiylx.build_logic.android.library"
            implementationClass = "plugin.AndroidLibraryConventionPlugin"
        }
        register("androidCompose") {
            id = "kiylx.build_logic.android.compose"
            implementationClass = "plugin.AndroidComposeConventionPlugin"
        }
    }
}
