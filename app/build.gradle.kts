plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("plugin.serialization") version "1.9.0"
}

android {
    namespace = "com.kiylx.weather"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.kiylx.weather"
        minSdk = 26
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"))
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Android.compose_version
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/versions/9/previous-compilation-data.bin"
        }
    }
}

dependencies {
    implementation(AndroidX.Core.core)
    implementation(AndroidX.appCompat)
    //Coroutines
    implementation(Coroutines.android)
    //lifecycle
    //implementation(AndroidX.Lifecycle.runtime)
    implementation(AndroidX.Lifecycle.livedata)
    //ktx
    implementation(AndroidX.Lifecycle.viewmodel)
    implementation(AndroidX.Lifecycle.fragment)
    implementation(AndroidX.Lifecycle.activity)
    //权限申请
    implementation(Common.perms)
    //lib
    implementation(project(":libx"))
    implementation(project(":icon"))
    implementation(project(":compose_lib"))

    //compose
    implementation(ComposeLibs.activityCompose)
    implementation(ComposeLibs.composeUi)
    implementation(ComposeLibs.composeUiToolingPreview)
    implementation(ComposeLibs.composeFoundation)
    implementation(ComposeLibs.composeMaterial3)
    implementation(ComposeLibs.composeRuntime)
    implementation(ComposeLibs.navigation)
    implementation(Accompanist.systemuicontroller)
    implementation(ComposeLibs.composeConstraintLayout)
    implementation(ComposeLibs.composeMaterialIconsExtended)
    //下拉刷新，上拉加载
    implementation("io.github.loren-moon:composesmartrefresh:1.2.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    //图表
    // https://mvnrepository.com/artifact/com.himanshoe/charty
//    implementation("com.himanshoe:charty:2.0.0-alpha01")

    //test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:${Android.compose_version}")
    debugImplementation("androidx.compose.ui:ui-tooling:${Android.compose_version}")
    debugImplementation("androidx.compose.ui:ui-test-manifest:${Android.compose_version}")

    // For AppWidgets support
    implementation ("androidx.glance:glance-appwidget:1.0.0")
    // For interop APIs with Material 3
    implementation ("androidx.glance:glance-material3:1.0.0")
}