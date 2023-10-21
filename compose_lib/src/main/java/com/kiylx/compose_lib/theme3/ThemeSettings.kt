package com.kiylx.compose_lib.theme3

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.google.android.material.color.DynamicColors
import com.kiylx.compose_lib.R
import com.kiylx.compose_lib.common.boolM
import com.kiylx.compose_lib.common.doubleM
import com.kiylx.compose_lib.common.intM
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 一些主题偏好值用到的key字符串
 */
class ThemeStr {
    companion object {
        const val use_default_theme = "use_default_theme"
        const val PALETTE_STYLE = "palette_style"
        const val DYNAMIC_COLOR = "dynamic_color"
        const val HIGH_CONTRAST = "high_contrast"
        const val dark_theme_mode = "dark_theme_mode"
        const val dark_use_contrast_mode = "dark_use_contrast_mode"
        const val light_theme_high_contrast_value = "light_theme_high_contrast_value"
        const val dark_theme_high_contrast_value = "dark_theme_high_contrast_value"
        const val seed_color_int = "theme_seed_color_int"
    }
}

/**
 * ```
 * 动态设置主题：
 * scope.launch {
 *       modifyThemeSeedColor(0xFF6750A4,PaletteStyle.Vibrant.ordinal)
 *  }
 * ```
 */
object ThemeHelper {
    const val DEFAULT_COLOR_SEED: Int = 0xFF6750A4.toInt()
    const val DEFAULT_HIGH_CONTRAST_VALUE_0 = 0.0
    const val DEFAULT_HIGH_CONTRAST_VALUE_1 = 0.0
    const val DEFAULT_DARK_MODE = DarkThemePrefs.FOLLOW_SYSTEM

    //<editor-fold desc="偏好值">
    val kv = MMKV.defaultMMKV()

    var darkThemeMode by kv.intM(ThemeStr.dark_theme_mode, DEFAULT_DARK_MODE)

    /**
     * 用于亮色主题
     */
    var lightThemeHighContrastValue by kv.doubleM(
        ThemeStr.light_theme_high_contrast_value,
        DEFAULT_HIGH_CONTRAST_VALUE_0
    )

    /**
     * 用于暗色主题
     */
    var darkThemeHighContrastValue by kv.doubleM(
        ThemeStr.dark_theme_high_contrast_value,
        DEFAULT_HIGH_CONTRAST_VALUE_1
    )

    var isUseDynamicColor by kv.boolM(
        ThemeStr.DYNAMIC_COLOR,
        DynamicColors.isDynamicColorAvailable()
    )

    /**
     * 暗色模式是否使用高对比度
     */
    var isDarkUseContrastMode by kv.boolM(
        ThemeStr.dark_use_contrast_mode,
        false
    )

    var useDefaultTheme by kv.boolM(
        ThemeStr.use_default_theme,
        true,
    )

    var seedColorInt by kv.intM(ThemeStr.seed_color_int, DEFAULT_COLOR_SEED)

    var paletteStyleInt by kv.intM(ThemeStr.PALETTE_STYLE, PaletteStyle.Monochrome.ordinal)

    //</editor-fold>

    //<editor-fold desc="主题配置">
    private val themeSettingsFlow = MutableStateFlow(
        ThemeSettings(
            darkTheme = DarkThemePrefs(
                darkThemeMode = darkThemeMode,
                isHighContrastModeEnabled = isDarkUseContrastMode,
                contrastValue = darkThemeHighContrastValue,
            ),
            isDynamicColorEnabled = isUseDynamicColor,
            themeColorSeed = seedColorInt,
            paletteStyleIndex = paletteStyleInt,
            useDefaultTheme = useDefaultTheme,
        )
    )
    val AppSettingsStateFlow = themeSettingsFlow.asStateFlow()

    //</editor-fold>
//<editor-fold desc="主题修改功能，调用下面的方法动态切换主题">

    fun CoroutineScope.recoveryDefaultTheme(useDefaultTheme: Boolean) {
        if (this@ThemeHelper.useDefaultTheme != useDefaultTheme) {
            launch(Dispatchers.IO) {
                themeSettingsFlow.update {
                    it.copy(useDefaultTheme = useDefaultTheme)
                }
                this@ThemeHelper.useDefaultTheme = useDefaultTheme
            }
        }
    }

    fun CoroutineScope.modifyDarkThemePreference(
        darkThemeMode: Int = AppSettingsStateFlow.value.darkTheme.darkThemeMode,
        isHighContrastModeEnabled: Boolean = AppSettingsStateFlow.value.darkTheme.isHighContrastModeEnabled,
        highContrastValue: Double = AppSettingsStateFlow.value.darkTheme.contrastValue,
    ) {
        launch(Dispatchers.IO) {
            themeSettingsFlow.update {
                it.copy(
                    darkTheme = AppSettingsStateFlow.value.darkTheme.copy(
                        darkThemeMode = darkThemeMode,
                        isHighContrastModeEnabled = isHighContrastModeEnabled,
                        contrastValue = highContrastValue
                    ),
                )
            }
            this@ThemeHelper.darkThemeMode = darkThemeMode
            this@ThemeHelper.isDarkUseContrastMode = isHighContrastModeEnabled
            this@ThemeHelper.darkThemeHighContrastValue = highContrastValue
        }
    }

    fun CoroutineScope.modifyThemeSeedColor(
        colorArgb: Int,
        paletteStyleIndex: Int,
        lightHighContrastValue: Double = lightThemeHighContrastValue
    ) {
        launch(Dispatchers.IO) {
            themeSettingsFlow.update {
                it.copy(
                    themeColorSeed = colorArgb,
                    paletteStyleIndex = paletteStyleIndex
                )
            }
            this@ThemeHelper.seedColorInt = colorArgb
            this@ThemeHelper.paletteStyleInt = paletteStyleIndex
            this@ThemeHelper.lightThemeHighContrastValue = lightHighContrastValue
        }
    }

    fun CoroutineScope.switchDynamicColor(enabled: Boolean = !themeSettingsFlow.value.isDynamicColorEnabled) {
        //仅在不同时更新
        if (enabled != this@ThemeHelper.isUseDynamicColor) {
            launch(Dispatchers.IO) {
                themeSettingsFlow.update {
                    it.copy(isDynamicColorEnabled = enabled)
                }
                this@ThemeHelper.isUseDynamicColor = enabled
            }
        }
    }

    /**
     * 获取当前主题的对比度值
     */
    @Composable
    fun currentThemeContrastValue(): Double {
        val darkPref = DarkThemePrefs(
            darkThemeMode = darkThemeMode,
            isHighContrastModeEnabled = isDarkUseContrastMode,
            contrastValue = darkThemeHighContrastValue,
        )
        return if (darkPref.isDarkTheme() && darkPref.isHighContrastModeEnabled) {
            darkPref.contrastValue
        } else {
            lightThemeHighContrastValue
        }
    }
//</editor-fold>
}

data class ThemeSettings(
    val darkTheme: DarkThemePrefs = DarkThemePrefs(),//暗色模式的设置
    val isDynamicColorEnabled: Boolean = false, //是否使用动态颜色
    val themeColorSeed: Int = ThemeHelper.DEFAULT_COLOR_SEED, //主题种子
    val paletteStyleIndex: Int = PaletteStyle.TonalSpot.ordinal, //主题调色板样式index
    val useDefaultTheme: Boolean = true,//使用默认主题
)

data class DarkThemePrefs(
    val darkThemeMode: Int = OFF,//暗色模式
    val isHighContrastModeEnabled: Boolean = false,
    val contrastValue: Double = 0.0,
) {
    companion object {
        const val FOLLOW_SYSTEM = 1
        const val ON = 2
        const val OFF = 3

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

inline val Int.toColor: Color
    @Composable get() {
        return Color(this)
    }
