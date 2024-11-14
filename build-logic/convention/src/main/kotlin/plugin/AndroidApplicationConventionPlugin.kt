package plugin

import com.android.build.api.dsl.ApplicationExtension
import com.kiylx.common.dependences.AndroidBuildCode
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.get


//通用的构建逻辑
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        println("构建 app module")

        with(target) {
            //配置plugin
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
                apply("kotlin-parcelize")
            }

            //或者可以这么写配置plugin
//            plugins.run {
//                apply("com.android.application")
//                apply("org.jetbrains.kotlin.android")
//            }
            val that = this

            //配置android
            extensions.configure<ApplicationExtension> {
                compileSdk = AndroidBuildCode.compileSdk
                defaultConfig {
                    minSdk = AndroidBuildCode.minSdk
                    targetSdk = AndroidBuildCode.targetSdk
                    versionCode = AndroidBuildCode.versionCode
                    versionName = AndroidBuildCode.versionName
//                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//                    vectorDrawables {
//                        useSupportLibrary = true
//                    }
//                    multiDexEnabled = true
                    ndk {
                        abiFilters.addAll(AndroidBuildCode.abi)
                    }
                }
                buildTypes {
                    debug {
                        applicationIdSuffix = ".debug"
                        isMinifyEnabled = false
                        isShrinkResources = false
                    }
                    release {
                        isMinifyEnabled = true
//                        signingConfig = signingConfigs["release"]
                        isShrinkResources = true
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro"
                        )
                    }
                }
                compileOptions {
                    //        isCoreLibraryDesugaringEnabled=true
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }
                buildFeatures {
                    viewBinding = true
                    buildConfig =true
                }
                kotlinOptions {
                    jvmTarget = JavaVersion.VERSION_17.toString()
                }
                packagingOptions {
                    resources {
                        excludes += "/META-INF/{AL2.0,LGPL2.1}"
                        excludes += "META-INF/versions/9/previous-compilation-data.bin"
                    }
                }
            }
        }
    }
}



