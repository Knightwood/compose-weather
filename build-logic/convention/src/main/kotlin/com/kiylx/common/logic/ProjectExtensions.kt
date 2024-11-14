package com.kiylx.common.logic


import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

//快速访问libs和composeLibs的扩展属性
//因为plugin会在某个module中使用，那么，这里通过gradle的project获取VersionCatalog时，
//名称得是那个gradle project中已注册catalogs文件时的名称，
//所以，定义变量查询VersionCatalog时，使用的是Project作为接受者，如果使用DependencyHandlerScope作为接收者，会找不到。

//错误示范：
//val DependencyHandlerScope.libs2
//    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>()
//        .named("buildLibs")
//

//比如在app module的 build.gradle.kt中应用插件，那么，这里就只能是在项目的setting.gradle.kt文件中创建catalogs文件的那个名称，
//而不是build-logic module的 setting.gradle.kt 中创建的那个名称。
//如果插件是在build-logic的build.gradle.kt中应用插件，就得是在build-logic的setting.gradle.kt文件中创建catalogs文件的那个名称
//所以，直接让build-logic模块setting.gradle.kt文件中注册的名称和项目的setting.gradle.kt文件中注册的名称一致，即可不用关心名称错乱问题
val Project.libs2
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>()
        .named("buildLibs")

val Project.composeLibs2
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>()
        .named("composeLibs")
val Project.otherLibs2
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>()
        .named("others")

//object ProjectExtensions {
//   lateinit var libs:VersionCatalog
//       internal set
//   lateinit var composeLibs:VersionCatalog
//       internal set
//
//}
/**
 * name不是依赖库的名字，而是catalogs文件中定义的名字，例如catalogs文件中有如下依赖
 * buildLogic-android-app-compose = { id = "kiylx.build_logic.android.application.compose", version = "unspecified" }
 * 那么，此方法传入的name就得是"buildLogic-android-app-compose"
 */
fun VersionCatalog.libFind(libName: String) = findLibrary(libName).get()

//引入依赖
val implementationDeps: String
    get() = "implementation"
val compileOnlyDeps: String
    get() = "compileOnly"

val annotationProcessorDeps: String
    get() = "annotationProcessor"

/**
 * 对于引入注解处理器，如果是java项目，可使用annotationProcessor("注解处理器库")
 * 对于kotlin项目，使用kap("注解处理器库")，kapt兼容java的注解处理。
 * 还可以用ksp取代kapt,使用ksp("注解处理器库")
 */
val kaptDeps: String get() = "kapt"
val kspDeps: String get() = "ksp"
val testImplementationDeps: String get() = "testImplementation"

val apiDeps: String get() = "api"

val debugImplementationDeps: String get() = "debugImplementation"

val androidTestImplementationDeps: String get() = "androidTestImplementation"
