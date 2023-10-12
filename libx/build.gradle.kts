plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    kotlin("plugin.serialization") version "1.9.0"
}

android {
    namespace = "com.kiylx.libx"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
        targetSdk = 32

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
}

dependencies {
    implementation(AndroidX_KTX.core)
    implementation(AndroidX.appCompat)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

    //Retrofit
    api(Retrofit2.core)
    api(Retrofit2.logging)//日志打印
    api(Retrofit2.converterGson)
    api(Retrofit2.converterScalars)
    api(Kotlin.serialization160rc)
    api(Retrofit2.converterKotlin)
    //Coroutines
    api(Coroutines.android)

    implementation(Lifecycle_KTX.livedata)
    implementation(Lifecycle_KTX.viewmodel)

    implementation(AndroidX_KTX.fragment)
    implementation(AndroidX_KTX.activity)
    //权限申请
    implementation(Tools.perms)
    //工具库
    api(Tools.utilcodex)
    api(Tools.mmkv)
}