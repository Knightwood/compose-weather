package com.kiylx.compose_lib.theme3

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

//<editor-fold desc="提供的compose函数中全局可用的主题配置">
val LocalDarkThemePrefs = compositionLocalOf { DarkThemePrefs() }
val LocalSeedColor = compositionLocalOf { ThemeHelper.seedColorInt }
val LocalWindowWidthState = staticCompositionLocalOf { WindowWidthSizeClass.Compact }
val LocalDynamicColorSwitch = compositionLocalOf { ThemeHelper.isUseDynamicColor }
val LocalPaletteStyleIndex = compositionLocalOf { ThemeHelper.paletteStyleInt }

/**
 * 存储当前的主题色，其实作用跟[MaterialTheme.colorScheme]一样
 */
val LocalColorScheme = staticCompositionLocalOf {
    mDynamicColorScheme(
        Color(ThemeHelper.seedColorInt),
        false,
        PaletteStyle.TonalSpot,
        ThemeHelper.lightThemeHighContrastValue
    )
}
//</editor-fold>


/**
 * 观察[ThemeHelper.AppSettingsStateFlow]配置，根据配置的更改生成新的ColorScheme从而刷新主题
 */
@Composable
fun ThemeSettingsProvider(
    windowWidthSizeClass: WindowWidthSizeClass,
    content: @Composable () -> Unit
) {
    ThemeHelper.AppSettingsStateFlow.collectAsState().value.run {
        //是否是暗色模式
        val isDark: Boolean = darkTheme.isDarkTheme()

        CompositionLocalProvider(
            LocalDarkThemePrefs provides darkTheme,
            LocalSeedColor provides themeColorSeed,
            LocalPaletteStyleIndex provides paletteStyleIndex,
            LocalColorScheme provides if (isDynamicColorEnabled && Build.VERSION.SDK_INT >= 31) {
                //系统的自动主题
                if (isDark) {
                    dynamicDarkColorScheme(LocalContext.current)
                } else {
                    dynamicLightColorScheme(LocalContext.current)
                }
            } else {
                val contrastValue = if (isDark && darkTheme.isHighContrastModeEnabled) {
                    ThemeHelper.darkThemeHighContrastValue
                } else {
                    ThemeHelper.lightThemeHighContrastValue
                }
                //手动的主题设置
                mDynamicColorScheme(
                    themeColorSeed.toColor,
                    isDark,
                    PaletteStyle.values()[paletteStyleIndex],
                    contrastValue
                )
            },
            LocalWindowWidthState provides windowWidthSizeClass,
            LocalDynamicColorSwitch provides isDynamicColorEnabled,
            content = content
        )
    }
}