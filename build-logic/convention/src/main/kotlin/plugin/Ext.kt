package plugin

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.api.AndroidBasePlugin
import com.kiylx.common.logic.libFind
import com.kiylx.common.logic.otherLibs2
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.jetbrains.kotlin.gradle.plugin.KaptExtension

fun Project.kaptConfig(block: KaptExtension.() -> Unit) {
    (this as ExtensionAware).extensions.configure("kapt", block)
}

fun CommonExtension<*, *, *, *, *, *>.kotlinOptions(block: KotlinJvmOptions.() -> Unit) {
    (this as ExtensionAware).extensions.configure("kotlinOptions", block)
}

/**
 * 判断是library module还是app module
 */
fun Project.parseLibraryOrApp2GetExtension(block: CommonExtension<*, *, *, *, *, *>.(isLibrary: Boolean) -> Unit) {
    val isLibrary: Boolean = pluginManager.hasPlugin("com.android.library")
    val extension = if (isLibrary) {
        extensions.getByType<LibraryExtension>()
    } else {
        extensions.getByType<ApplicationExtension>()
    }
    extension.block(isLibrary)
}

/**
 * Extension config
 *
 * 解析配置类，并且根据project的插件类型，执行不同的配置
 *
 * @param project
 * @param publicType
 * @param name
 * @param instanceType
 * @param constructionArguments
 * @param action
 * @param T
 * @receiver
 */
fun <T> extensionConfig(
    project: Project,

    publicType: Class<T>,
    name: String,
    instanceType: Class<out T>,
    constructionArguments: Array<Any>? = null,
    action: CommonExtension<*, *, *, *, *, *>.(isLibrary: Boolean, config: T) -> Unit
) {
    project.plugins.withType(AndroidBasePlugin::class.java) {
        val extensionConfig = if (constructionArguments != null) {
                project.extensions.create(publicType, name, instanceType, constructionArguments)
            } else {
                project.extensions.create(publicType, name, instanceType)
            }
        //我不明白为什么只有在afterEvaluate才能读取到用户修改后的配置，否则只能读取到默认值
        //可能插件在module的build.gradle.kts中应用插件的时候，代码还没有走到用户修改配置的地方，换言之，就是太早了
        //可是，配置没法写在插件生效之前。因为插件生效之前，配置类还没有创建出来。
        project.afterEvaluate {
            parseLibraryOrApp2GetExtension {
                action(it, extensionConfig)
            }
        }
    }
}

/**
 * Extension config
 *
 * 作用同上
 */
fun <T> extensionConfig(
    project: Project,

    name: String,
    instanceType: Class<out T>,
    constructionArguments: Array<Any>? = null,
    action: CommonExtension<*, *, *, *, *, *>.(isLibrary: Boolean, config: T) -> Unit
) {
    project.plugins.withType(AndroidBasePlugin::class.java) {
        val extensionConfig = if (constructionArguments != null) {
                project.extensions.create(name, instanceType, constructionArguments)
            } else {
                project.extensions.create(name, instanceType)
            }
        project.afterEvaluate {
            parseLibraryOrApp2GetExtension {
                action(it, extensionConfig)
            }
        }
    }
}

