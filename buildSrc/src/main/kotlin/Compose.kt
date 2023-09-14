
object Accompanist {
    private const val group="com.google.accompanist"
    private const val version = "0.31.3-beta"
    const val navigationAnimation = "$group:accompanist-navigation-animation:$version"
    const val permissions = "$group:accompanist-permissions:$version"
    const val systemuicontroller = "$group:accompanist-systemuicontroller:$version"
    const val webview = "$group:accompanist-webview:$version"
    const val pagerLayouts = "$group:accompanist-pager:$version"
    const val pagerIndicators = "$group:accompanist-pager-indicators:$version"
    const val flowlayout = "$group:accompanist-flowlayout:$version"
}
object ComposeLibs{ 
    private const val lifecycleVersion = "2.6.1"
   const val lifecycleRuntimeCompose = "androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion"
   const val lifecycleViewModelCompose = "androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion"

    const val composeRuntime = "androidx.compose.runtime:runtime:${Android.compose_version}"
    const val composeUiToolingPreview = "androidx.compose.ui:ui-tooling-preview:${Android.compose_version}"
    const val composeFoundation = "androidx.compose.foundation:foundation:${Android.compose_version}"
    const val composeUi = "androidx.compose.ui:ui:${Android.compose_version}"

    const val composeMaterialIconsExtended = "androidx.compose.material:material-icons-extended:1.3.1"
//    const val composeAnimation = "androidx.compose.animation:animation"
//    const val composeMaterial2 = "androidx.compose.material:material"
    const val composeMaterial3 = "androidx.compose.material3:material3:1.2.0-alpha03"
    const val composeMaterial3WindowSizeClass = "androidx.compose.material3:material3-window-size-class:1.2.0-alpha03"
    const val activityCompose = "androidx.activity:activity-compose:1.7.2"

    const val composeConstraintLayout ="androidx.constraintlayout:constraintlayout-compose:1.1.0-alpha07"

    const val nav_version = "2.7.0"
    const val navigation = "androidx.navigation:navigation-compose:$nav_version"

}
object ComposeTest{
    const val composeUiTest = "androidx.compose.ui:ui-test-junit4:${Android.compose_version}"
    const val composeUiTooling = "androidx.compose.ui:ui-tooling:${Android.compose_version}"
    const val composeTestManifist="androidx.compose.ui:ui-test-manifest:${Android.compose_version}"
}

object ComposeOhterLibs{
    object Coil {
        private const val coilVersion = "2.4.0"
        const val coil = "io.coil-kt:coil-compose:$coilVersion"
    }
}