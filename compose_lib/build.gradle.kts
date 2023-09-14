plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.kiylx.compose_lib"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

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
    implementation(ComposeLibs.activityCompose)
    implementation("androidx.compose.ui:ui:${Android.compose_version}")
    implementation("androidx.compose.ui:ui-tooling-preview:${Android.compose_version}")
    implementation(ComposeLibs.composeMaterial3)
    implementation(ComposeLibs.composeFoundation)
    implementation(Accompanist.systemuicontroller)
//    implementation(Accompanist.flowlayout)
    implementation(ComposeLibs.composeRuntime)
    implementation(ComposeLibs.navigation)
    implementation(ComposeLibs.composeMaterialIconsExtended)

    implementation(AndroidX.appCompat)
    implementation(AndroidX.Core.core)
    api("androidx.compose.material3:material3-window-size-class:1.1.1")
    implementation("com.google.android.material:material:1.9.0")


    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

    implementation("com.tencent:mmkv:1.3.0")
    implementation("com.caverock:androidsvg-aar:1.4")
    api(ComposeOhterLibs.Coil.coil)
}