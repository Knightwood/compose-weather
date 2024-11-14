plugins {
    alias(buildLibs.plugins.buildLogic.android.library)
    alias(buildLibs.plugins.buildLogic.android.compose)
}

android.namespace = "com.kiylx.compose_lib"

dependencies {
    implementation(buildLibs.bundles.bundleAndroidx)
    implementation(buildLibs.bundles.kotlins)
    implementation(buildLibs.bundles.retrofit2)
    implementation(others.github.perms)
    implementation(composeLibs.androidx.navigation.compose)
    implementation(composeLibs.google.accompanist.systemUiController)
    implementation(others.coil.kt.compose)
    implementation(composeLibs.google.accompanist.systemUiController)
    //datastore
    implementation(buildLibs.androidx.datastore)
    implementation(buildLibs.androidx.datastore.preferences)
    implementation(others.github.svgSupport)
    implementation(others.github.mmkv)
    implementation(others.github.knightwood.m3ColorUtilities)
}