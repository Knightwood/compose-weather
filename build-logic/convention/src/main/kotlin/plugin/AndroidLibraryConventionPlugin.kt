package plugin

import com.android.build.api.dsl.LibraryExtension
import com.kiylx.common.dependences.AndroidBuildCode
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure


//通用的构建逻辑
class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        println("构建 library module")

        with(target) {
            val that = this

            //配置plugin
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
                apply("kotlin-parcelize")
            }
            //配置android
            extensions.configure<LibraryExtension> {
                compileSdk = AndroidBuildCode.compileSdk
                defaultConfig {
                    minSdk = AndroidBuildCode.minSdk
                    lint.targetSdk = AndroidBuildCode.targetSdk
//                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//                    vectorDrawables {
//                        useSupportLibrary = true
//                    }
                    consumerProguardFiles("consumer-rules.pro")
                    ndk {
                        abiFilters.addAll(AndroidBuildCode.abi)
                    }
                }

                buildTypes {
                    release {
                        isMinifyEnabled = false
                        consumerProguardFiles("consumer-rules.pro")
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro"
                        )
                    }
                }

                packagingOptions {
                    resources {
                        excludes += "/META-INF/{AL2.0,LGPL2.1}"
                    }
                }
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }
                buildFeatures {
                    viewBinding =true
                    buildConfig =true
                }
                kotlinOptions {
                    jvmTarget = JavaVersion.VERSION_17.toString()
                }
            }
        }
    }
}

