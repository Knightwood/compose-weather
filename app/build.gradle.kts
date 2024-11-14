plugins {
    alias(buildLibs.plugins.buildLogic.android.app)
    alias(buildLibs.plugins.kotlin.serialization)
    alias(buildLibs.plugins.buildLogic.android.compose)
}

android {
    namespace = "com.kiylx.weather"
    defaultConfig {
        applicationId = "com.kiylx.weather"
    }
}

dependencies {
    implementation(buildLibs.bundles.bundleAndroidx)
    implementation(buildLibs.bundles.kotlins)
    implementation(buildLibs.bundles.retrofit2)
    implementation(others.github.perms)
    implementation(others.github.mmkv)
    implementation(composeLibs.androidx.navigation.compose)
    implementation(others.github.utilcodex)
    //lib
    implementation(project(":libx"))
    implementation(project(":icon"))
    implementation(project(":compose_lib"))
    implementation(buildLibs.androidx.work.runtime.ktx)
    // For AppWidgets support
    implementation(composeLibs.androidx.glance.material3)
    implementation(composeLibs.androidx.glance.appwidget)
}