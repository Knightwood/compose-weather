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

    //compose
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.compose.ui:ui:${Android.compose_version}")
    implementation("androidx.compose.ui:ui-tooling-preview:${Android.compose_version}")
    implementation("androidx.compose.foundation:foundation:1.5.0")
    implementation("androidx.compose.material3:material3:1.2.0-alpha03")
//    implementation("androidx.compose.material3:material3-window-size-class:1.2.0-alpha03")
    implementation("androidx.compose.runtime:runtime:1.5.0")
    val nav_version = "2.7.0"
    implementation("androidx.navigation:navigation-compose:$nav_version")
    implementation(Accompanist.systemuicontroller)
//    implementation(Accompanist.pagerLayouts)
    implementation(ComposeLibs.composeMaterialIconsExtended)
    //下拉刷新，上拉加载
    implementation("io.github.loren-moon:composesmartrefresh:1.2.1")

    //test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:${Android.compose_version}")
    debugImplementation("androidx.compose.ui:ui-tooling:${Android.compose_version}")
    debugImplementation("androidx.compose.ui:ui-test-manifest:${Android.compose_version}")
}