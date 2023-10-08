@file:Suppress(
    "unused",
    "SpellCheckingInspection",
)

object AndroidX {
    private const val fragment_version = "1.6.1"
    private const val activity_version = "1.8.0"

    //api了 core：1.9.0； fragment:1.3.6; activity:1.6.0
    const val appCompat = "androidx.appcompat:appcompat:1.6.1"
    const val desugar = "com.android.tools:desugar_jdk_libs:1.2.2"
    const val recyclerView = "androidx.recyclerview:recyclerview:1.2.1"

    //material 1.9 库api了 appcompat:1.5.0; cardview:1.0.0; constraintlayout:2.0.1; coordinatorlayout:1.1.0;
    //androidx.core.core:1.6.0; drawerlayout:1.1.1; fragment:1.2.5; lifecycle-runtime:2.0.0; recyclerview:1.0.0
    //viewpager2:1.0.0; transition:1.2.0 等库
    const val material = "com.google.android.material:material:1.9.0"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.1.4"

    const val activity="androidx.activity:activity:$activity_version"
    const val fragment="androidx.fragment:fragment:$fragment_version"
}

object AndroidX_KTX {
    private const val fragment_version = "1.6.1"
    private const val activity_version = "1.8.0"

    const val fragment = "androidx.fragment:fragment-ktx:$fragment_version"
    const val activity = "androidx.activity:activity-ktx:$activity_version"

    //preference-ktx api了 fragment-ktx:1.3.6; preference:1.2.0; core-ktx:1.1.0; kotlin-stdlib:1.6.0 等
    //跟appcompat有点冲突，exclude(group = "androidx.lifecycle", module = "lifecycle-viewmodel-ktx")
    const val preference = "androidx.preference:preference-ktx:1.2.0"

    const val core_version = "1.8.0"
    const val core = "androidx.core:core-ktx:$core_version"
}

object Lifecycle_KTX {
    private const val lifecycle_version = "2.6.2"

    // Lifecycles only (without ViewModel or LiveData)
    const val runtime = "androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version"

    // Lifecycle utilities for Compose
    const val runtimeCompose = "androidx.lifecycle:lifecycle-runtime-compose:$lifecycle_version"

    // Saved state module for ViewModel
    const val savestate = "androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycle_version"

    // LiveData
    const val livedata = "androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version"

    // ViewModel
    const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version"

    // ViewModel utilities for Compose
    const val viewmodelCompose =
        "androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version"

    // optional - helpers for implementing LifecycleOwner in a Service
    const val serviceLifecycle = "androidx.lifecycle:lifecycle-service:$lifecycle_version"

    // optional - ProcessLifecycleOwner provides a lifecycle for the whole application process
    const val lifecycleProcess = "androidx.lifecycle:lifecycle-process:$lifecycle_version"

}


object Coil {
    private const val coilVersion = "2.2.2"
    const val coil = "io.coil-kt:coil:$coilVersion"
}


object Datastore {
    private const val version = "1.0.0"

    // Preferences DataStore（可以直接使用）
    const val datastorePrefs = "androidx.datastore:datastore-preferences:$version"

    // Preferences DataStore （没有Android依赖项，包含仅适用于 Kotlin 的核心 API）
    const val datastorePrefsCore = "androidx.datastore:datastore-preferences-core:$version"

    // Typed DataStore （Proto DataStore）
    const val datastore = "androidx.datastore:datastore:$version"

    // Typed DataStore （没有Android依赖项，包含仅适用于 Kotlin 的核心 API）
    const val datastoreCore = "androidx.datastore:datastore-core:$version"

}

object Excludes {
    const val jniExclude = "/okhttp3/internal/publicsuffix/*"
    val listExclude: List<String> = listOf(
        "/DebugProbesKt.bin",
        "/kotlin/**.kotlin_builtins",
        "/kotlin/**.kotlin_metadata",
        "/META-INF/**.kotlin_module",
        "/META-INF/**.pro",
        "/META-INF/**.version",
        "/okhttp3/internal/publicsuffix/*"
    )
}

object Navigation {
    private const val nav_version = "2.7.4"
    const val runtime_ktx = "androidx.navigation:navigation-runtime-ktx:$nav_version"
    const val fragment_ktx = "androidx.navigation:navigation-fragment-ktx:$nav_version"
    const val ui_ktx = "androidx.navigation:navigation-ui-ktx:$nav_version"
    const val dynamicFeature =
        "androidx.navigation:navigation-dynamic-features-fragment:$nav_version"

    // Java language implementation
    const val fragment = "androidx.navigation:navigation-fragment:$nav_version"
    const val ui = "androidx.navigation:navigation-ui:$nav_version"
}

object Hilt {
    private const val version = "2.44.2"
    const val classpath = "com.google.dagger:hilt-android-gradle-plugin:$version"
    const val android = "com.google.dagger:hilt-android:$version"

    const val compiler = "com.google.dagger:hilt-compiler:$version"
    const val plugin = "dagger.hilt.android.plugin"
    private const val androidXHilt = "1.0.0"
    const val work = "androidx.hilt:hilt-work:$androidXHilt"
    const val androidX = "androidx.hilt:hilt-compiler:$androidXHilt"
}
object Test {
    const val jUnitRunner = "androidx.test.runner.AndroidJUnitRunner"
    const val jUnit = "junit:junit:4.13.2"
    const val androidJUnit = "androidx.test.ext:junit:1.1.3"
    const val espresso = "androidx.test.espresso:espresso-core:3.4.0"
}
object Work {
    private const val version = "2.8.1"
    const val manager = "androidx.work:work-runtime-ktx:$version"
}

object Room {
    private const val roomVersion = "2.5.0"
    const val runtime = "androidx.room:room-runtime:$roomVersion"
    const val compiler = "androidx.room:room-compiler:$roomVersion"
    const val ktx = "androidx.room:room-ktx:$roomVersion"
}

//==========================================================
object Jackson {
    const val core = "com.fasterxml.jackson.core:jackson-core:2.14.2"
}

object Kotlin {
    //kotlin序列化 kt 1.7.10
    const val serialization140 = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0"
    const val datetime = "org.jetbrains.kotlinx:kotlinx-datetime:0.4.0"

    //kotlin序列化 kt 1.6.21
    const val serialization133 = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3"

    //kotlin序列化 kt 1.8.10
    const val serialization150 = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0"

    //kotlin序列化 kt 1.9.0
    const val serialization160rc = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0-RC"
}

object Coroutines {
    private const val coroutinesVersion = "1.7.3"
    const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion"
    const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion"
}

object Ktor {
    private const val version = "2.2.3"
    const val core = "io.ktor:ktor-client-core:$version"
    const val okhttp = "io.ktor:ktor-client-okhttp:$version"
}


object SQLite {
    private const val version = "2.3.0"
    const val ktx = "androidx.sqlite:sqlite-ktx:$version"
}


object OkHttp {
    private const val version = "4.11.0"
    const val okhttp = "com.squareup.okhttp3:okhttp:$version"
}
object Retrofit2 {
    const val core = "com.squareup.retrofit2:retrofit:2.9.0"
    const val converterScalars = "com.squareup.retrofit2:converter-scalars:2.6.2"
    const val converterGson = "com.squareup.retrofit2:converter-gson:2.9.0"
    const val logging = "com.squareup.okhttp3:logging-interceptor:4.2.0"

    /**
     * 打印okhttp的log库
     * 使用logging2需要添加 exclude(group = "org.json", module = "json")
     */
    const val logging2 =
        "com.github.ihsanbal:LoggingInterceptor:3.1.0"

    //kotlin的转换器
    const val converterKotlin =
        "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0"

}

object Others {
    const val libsu = "com.github.topjohnwu.libsu:core:5.0.4"
    const val zoomage = "com.jsibbold:zoomage:1.3.1"
    private const val shizukuVersion = "13.0.0"
    const val shizukuApi = "dev.rikka.shizuku:api:$shizukuVersion"
    const val shizukuProvider = "dev.rikka.shizuku:provider:$shizukuVersion"
}
object Tools {
    //崩溃日志上传
    const val logReport = "com.github.wenmingvs:LogReport:1.0.3"

    //内存泄漏分析
    const val leakcanary = "com.squareup.leakcanary:leakcanary-android:2.8.1"

    //log库（不是okhttp的log库）
    const val logger = "com.orhanobut:logger:2.2.0"

    //crash捕获库
    const val crasher = "me.jfenn:crasher:0.0.2"

    //工具库
    const val utilcodex = "com.blankj:utilcodex:1.31.0"

    const val mmkv ="com.tencent:mmkv:1.3.0"
    //生成material design 3 的主题色
    const val m3Color ="com.github.Kyant0:m3color:2023.8.1"
    //svg
    const val svgSupport ="com.caverock:androidsvg-aar:1.4"


    //权限申请
    const val perms = "com.guolindev.permissionx:permissionx:1.6.1"

    //页面切换
    const val loadsir = "com.kingja.loadsir:loadsir:1.3.8"

    //载图
    //implementation("com.github.bumptech.glide:glide:4.10.0")
    //annotationProcessor("com.github.bumptech.glide:compiler:4.10.0")
    object SmartRefresh {
        //刷新
        const val refreshKernal = "io.github.scwang90:refresh-layout-kernel:2.0.5"
        const val refreshMaterialStyle = "io.github.scwang90:refresh-header-material:2.0.5"
    }
    //高德地图
    //implementation("com.amap.api:map2d:6.0.0")
    //implementation("com.amap.api:location:6.1.0")

    //recyclerview
    //implementation("com.github.CymChad:BaseRecyclerViewAdapterHelper:2.9.50")

    //indexRecyclerview
    //implementation("me.yokeyword:indexablerecyclerview:1.3.0")
    //implementation("com.contrarywind:Android-PickerView:4.1.9")

    //刘海屏适配
    //implementation("com.github.KilleTom:BangScreenToolsMaster:v1.0.0")

    //图表库
    //implementation("com.openxu.viewlib:OXViewLib:1.0.2")
    //implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    //日历库
    //implementation("com.necer.ncalendar:ncalendar:5.0.2")
    //环形视图
    //implementation("com.github.jakob-grabner:Circle-Progress-View:1.4")

    //app update
    const val appUpdate = "com.github.azhon:AppUpdate:4.0.0"
}

