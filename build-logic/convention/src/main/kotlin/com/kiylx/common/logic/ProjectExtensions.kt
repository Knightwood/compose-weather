package com.kiylx.common.logic


import org.gradle.api.artifacts.VersionCatalog

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
