plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.kiylx.compose_lib"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
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
        }
    }
}

dependencies {
//compose
    implementation(Compose.activityCompose)
    implementation("androidx.compose.ui:ui:${Android.compose_version}")
    implementation("androidx.compose.ui:ui-tooling-preview:${Android.compose_version}")
    implementation(Compose.composeMaterial3)
    implementation(Compose.composeFoundation)
    implementation(Accompanist.systemuicontroller)
//    implementation(Accompanist.flowlayout)
    implementation(Compose.composeRuntime)
    implementation(Compose.navigation)
    implementation(Compose.composeMaterialIconsExtended)

    implementation(AndroidX.appCompat)
    implementation(AndroidX_KTX.core)
    api(Compose.composeMaterial3WindowSizeClass)
    implementation(AndroidX.material)


    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

    implementation(Tools.mmkv)
    implementation(Tools.svgSupport)
    api(ComposeTools.Coil.coil)
    api(Tools.m3Color)
}