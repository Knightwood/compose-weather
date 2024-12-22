package plugin

import com.kiylx.common.logic.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project


class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        println("配置compose")
        with(target) {
            val that = this
            with(pluginManager) {
                apply("org.jetbrains.kotlin.plugin.compose")
            }
            parseLibraryOrApp2GetExtension {
                that.configureAndroidCompose(this,it)
            }
        }
    }
}