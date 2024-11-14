plugins {
    alias(buildLibs.plugins.buildLogic.android.library)
    alias(buildLibs.plugins.kotlin.serialization)
}

android.namespace = "com.kiylx.libx"

dependencies {
    implementation(buildLibs.bundles.bundleAndroidx)
    implementation(buildLibs.bundles.kotlins)
    implementation(buildLibs.bundles.retrofit2)
    implementation(others.github.perms)
    implementation(others.github.utilcodex)
    implementation(others.github.mmkv)
}