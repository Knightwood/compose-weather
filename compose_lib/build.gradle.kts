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
            isMinifyEnabled = true
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
    implementation(AndroidX.material)

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
    api(Compose.composeMaterialIconsExtended)
    api(Compose.composeMaterial3WindowSizeClass)
    implementation("androidx.compose.ui:ui:${Android.compose_version}")
    implementation("androidx.compose.ui:ui-tooling-preview:${Android.compose_version}")
    //datastore
    implementation(Datastore.datastorePrefs)
    implementation(Datastore.datastore)

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

    implementation(Tools.mmkv)
    implementation(Tools.svgSupport)
    api(ComposeTools.Coil.coil)
    api(Tools.m3Color)
}