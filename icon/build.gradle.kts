plugins {
    alias(buildLibs.plugins.buildLogic.android.library)
    alias(buildLibs.plugins.buildLogic.android.compose)
}

android.namespace = "com.kiylx.weather.icon"

dependencies {
    implementation(buildLibs.bundles.bundleAndroidx)
    implementation(buildLibs.bundles.kotlins)
    api(others.lottie.compose)
}