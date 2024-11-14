// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(buildLibs.plugins.android.application) apply false
    alias(buildLibs.plugins.android.library) apply false
    alias(buildLibs.plugins.kotlin.android) apply false
    alias(buildLibs.plugins.kotlin.serialization) apply false
    alias(buildLibs.plugins.compose.compiler) apply false
}