package com.kiylx.weather.ui.page.glance

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.glance.color.ColorProviders
import androidx.glance.material3.ColorProviders
import androidx.glance.unit.ColorProvider
import com.kiylx.compose_lib.theme3.DarkThemePrefs
import com.kiylx.compose_lib.theme3.PaletteStyle
import com.kiylx.compose_lib.theme3.ThemeHelper
import com.kiylx.compose_lib.theme3.default_theme.DefaultColorScheme
import com.kiylx.compose_lib.theme3.mDynamicColorScheme
import com.kiylx.compose_lib.theme3.toColor

object ThemeColorProvider {
    /**
     * across prefs to provide different color scheme
     */
    @Composable
    fun getColors(isDark:Boolean): ColorProviders {
        val scheme: ColorScheme = ThemeHelper.run {
            if (isUseDynamicColor && Build.VERSION.SDK_INT >= 31) {
                //系统的自动主题
                if (isDark) {
                    dynamicDarkColorScheme(LocalContext.current)
                } else {
                    dynamicLightColorScheme(LocalContext.current)
                }
            } else {
                val contrastValue = if (isDark && isDarkUseContrastMode) {
                    darkThemeHighContrastValue
                } else {
                    lightThemeHighContrastValue
                }
                if (useDefaultTheme) {
                    if (isDark) {
                        DefaultColorScheme.DarkColorScheme
                    } else {
                        DefaultColorScheme.LightColorScheme
                    }
                } else {
                    //手动的主题设置
                    mDynamicColorScheme(
                        seedColorInt.toColor,
                        isDark,
                        PaletteStyle.values()[paletteStyleInt],
                        contrastValue
                    )
                }
            }
        }

        return ColorProviders(
            scheme = scheme
        )
    }
}