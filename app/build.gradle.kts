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
        debug {
            applicationIdSuffix = ".debug"
            isMinifyEnabled = false
            isShrinkResources = false
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
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
    implementation(AndroidX_KTX.core)
    implementation(AndroidX.appCompat)
    //Coroutines
    implementation(Coroutines.android)
    //lifecycle
    implementation(Lifecycle_KTX.livedata)
    implementation(Lifecycle_KTX.viewmodel)
    //ktx
    implementation(AndroidX_KTX.fragment)
    implementation(AndroidX_KTX.activity)
    //权限申请
    implementation(Tools.perms)
    //lib
    implementation(project(":libx"))
    implementation(project(":icon"))
    implementation(project(":compose_lib"))

    //compose
    implementation(Compose.activityCompose)
    implementation(Compose.composeUi)
    implementation(Compose.composeUiToolingPreview)
    implementation(Compose.composeFoundation)
    implementation(Compose.composeMaterial3)
    implementation(Compose.composeRuntime)
    implementation(Compose.navigation)
    implementation(Accompanist.systemuicontroller)
    implementation(Compose.composeConstraintLayout)
    //下拉刷新，上拉加载
    implementation(ComposeTools.pullRefresh)
    implementation("me.omico.lux:lux-androidx-compose-material3-pullrefresh")
    implementation(Work.manager)

    //test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:${Android.compose_version}")
    debugImplementation("androidx.compose.ui:ui-tooling:${Android.compose_version}")
    debugImplementation("androidx.compose.ui:ui-test-manifest:${Android.compose_version}")

    // For AppWidgets support
    implementation(Compose.Glance.glance)
    // For interop APIs with Material 3
    implementation(Compose.Glance.glance_material3)
}