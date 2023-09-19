package com.kiylx.compose_lib.theme3

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.Window
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextDirection
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.material.color.MaterialColors

fun Color.applyOpacity(enabled: Boolean): Color {
    return if (enabled) this else this.copy(alpha = 0.62f)
}

@Composable
fun Color.harmonizeWith(other: Color) =
    Color(MaterialColors.harmonize(this.toArgb(), other.toArgb()))

@Composable
fun Color.harmonizeWithPrimary(): Color =
    this.harmonizeWith(other = MaterialTheme.colorScheme.primary)


private tailrec fun Context.findWindow(): Window? =
    when (this) {
        is Activity -> window
        is ContextWrapper -> baseContext.findWindow()
        else -> null
    }

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun Activity.DynamicTheme(
    content: @Composable () -> Unit
) {
    val windowSizeClass = calculateWindowSizeClass(this)
    ThemeSettingsProvider(windowWidthSizeClass = windowSizeClass.widthSizeClass) {
        val colorScheme = LocalColorScheme.current

        val window = LocalView.current.context.findWindow()
        val view = LocalView.current
        val isDark = LocalDarkThemePrefs.current.isDarkTheme()
        rememberSystemUiController(window).setSystemBarsColor(Color.Transparent,!isDark)
        ProvideTextStyle(
            value = LocalTextStyle.current.copy(
                lineBreak = LineBreak.Paragraph,
                textDirection = TextDirection.Content
            )
        ) {
            MaterialTheme(
                colorScheme = colorScheme,
                typography = Typography,
                content = content
            )
        }
    }

}

@Composable
fun PreviewThemeLight(
    color: Color=Color(ThemeHelper.DEFAULT_COLOR_SEED),
    content: @Composable () -> Unit
) {
   val colorsScheme= mDynamicColorScheme(color, false, PaletteStyle.values()[0], 0.0)
    MaterialTheme(
        colorScheme = colorsScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}