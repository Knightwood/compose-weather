package plugin

import com.kiylx.common.logic.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project


class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        println("配置compose")
        with(target) {
            /**
             * 注意这里的that，
             * extensions.getByType<VersionCatalogsExtension>()需要在project做接收者的情况下才能查找到catalogs文件
             * ,所以在这里定义了一下that去指向方法定义时的接收者，即project对象。
             * 而且，这里查找的catalogs文件，是依靠project的，而这里的project,是app module的build.gradle.kt
             * 因此，查找的catalogs文件也是在 app module 所处的环境定义/引入的catalogs文件，
             * 即，项目的setting.gradle.kt中创建的catalogs文件
             */
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