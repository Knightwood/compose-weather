package com.kiylx.compose_lib.theme3

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.google.android.material.color.DynamicColors
import com.kiylx.compose_lib.R
import com.kiylx.compose_lib.common.boolM
import com.kiylx.compose_lib.common.doubleM
import com.kiylx.compose_lib.common.intM
import com.kiylx.compose_lib.common.longM
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ThemeSettings(
    val darkTheme: DarkThemePrefs = DarkThemePrefs(),//暗色模式的设置
    val isDynamicColorEnabled: Boolean = false, //是否使用动态颜色
    val themeColorSeed: Long = ThemeHelper.DEFAULT_COLOR_SEED, //主题种子
    val paletteStyleIndex: Int = 0 //主题调色板样式index
)

/**
 * 一些主题偏好值用到的key字符串
 */
class ThemeStr {
    companion object {
        const val PALETTE_STYLE = "palette_style"
        const val DYNAMIC_COLOR = "dynamic_color"
        const val HIGH_CONTRAST = "high_contrast"
        const val dark_theme_mode = "dark_theme_mode"
        const val dark_theme_high_contrast_value = "dark_theme_high_contrast_value"
        const val seed_color_int = "theme_seed_color_int"
    }
}

/**
 * ```
 * 动态设置主题：
 * scope.launch {
 *       modifyThemeSeedColor(0xFF6750A4,3)
 *  }
 * ```
 */
object ThemeHelper {
    const val DEFAULT_COLOR_SEED = 0xFF6750A4

    //<editor-fold desc="偏好值">
    val kv = MMKV.defaultMMKV()

    var darkThemeMode by kv.intM(ThemeStr.dark_theme_mode, DarkThemePrefs.FOLLOW_SYSTEM)

    var darkThemeHighContrastValue by kv.doubleM(
        ThemeStr.dark_theme_high_contrast_value,
        DarkThemePrefs.defaultHighContrastValue
    )

    var isUseDynamicColor by kv.boolM(ThemeStr.DYNAMIC_COLOR, DynamicColors.isDynamicColorAvailable())

    var seedColorInt by kv.longM(ThemeStr.seed_color_int, DEFAULT_COLOR_SEED)

    var paletteStyleInt by kv.intM(ThemeStr.PALETTE_STYLE, PaletteStyle.TonalSpot.ordinal)

    //</editor-fold>

    //<editor-fold desc="主题配置">
    private val themeSettingsFlow = MutableStateFlow(
        ThemeSettings(
            darkTheme = DarkThemePrefs(
                darkThemeMode = darkThemeMode,
                highContrastValue = darkThemeHighContrastValue
            ),
            isDynamicColorEnabled = isUseDynamicColor,
            themeColorSeed = seedColorInt,
            paletteStyleIndex = paletteStyleInt
        )
    )
    val AppSettingsStateFlow = themeSettingsFlow.asStateFlow()

    //</editor-fold>
//<editor-fold desc="主题修改功能，调用下面的方法动态切换主题">
    fun CoroutineScope.modifyDarkThemePreference(
        darkThemeMode: Int = AppSettingsStateFlow.value.darkTheme.darkThemeMode,
        highContrastValue: Double = AppSettingsStateFlow.value.darkTheme.highContrastValue
    ) {
        launch(Dispatchers.IO) {
            themeSettingsFlow.update {
                it.copy(
                    darkTheme = AppSettingsStateFlow.value.darkTheme.copy(
                        darkThemeMode = darkThemeMode,
                        highContrastValue = highContrastValue
                    )
                )
            }
            this@ThemeHelper.darkThemeMode = darkThemeMode
            this@ThemeHelper.darkThemeHighContrastValue = highContrastValue
        }
    }

    fun CoroutineScope.modifyThemeSeedColor(colorArgb: Long, paletteStyleIndex: Int) {
        launch(Dispatchers.IO) {
            themeSettingsFlow.update {
                it.copy(themeColorSeed = colorArgb, paletteStyleIndex = paletteStyleIndex)
            }
            this@ThemeHelper.seedColorInt = colorArgb
            this@ThemeHelper.paletteStyleInt = paletteStyleIndex
        }
    }

    fun CoroutineScope.switchDynamicColor(enabled: Boolean = !themeSettingsFlow.value.isDynamicColorEnabled) {
        launch(Dispatchers.IO) {
            themeSettingsFlow.update {
                it.copy(isDynamicColorEnabled = enabled)
            }
            this@ThemeHelper.isUseDynamicColor = enabled
        }
    }
//</editor-fold>
}

data class DarkThemePrefs(
    val darkThemeMode: Int = FOLLOW_SYSTEM,//暗色模式
    val highContrastValue: Double = defaultHighContrastValue//默认对比度为0
) {
    companion object {
        const val FOLLOW_SYSTEM = 1
        const val ON = 2
        const val OFF = 3

        const val defaultHighContrastValue = 0.0
    }

    @Composable
    fun isDarkTheme(): Boolean {
        return if (darkThemeMode == FOLLOW_SYSTEM)
            isSystemInDarkTheme()
        else darkThemeMode == ON
    }

    @Composable
    fun getDarkThemeDesc(): String {
        return when (darkThemeMode) {
            FOLLOW_SYSTEM -> stringResource(R.string.follow_system)
            ON -> stringResource(R.string.on)
            else -> stringResource(R.string.off)
        }
    }

}

inline val Long.toColor: Color
    @Composable get() {
        return Color(this)
    }
