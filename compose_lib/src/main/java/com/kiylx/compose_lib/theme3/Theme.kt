package com.kiylx.compose_lib.theme3

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.Window
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextDirection
import androidx.core.view.WindowCompat
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


tailrec fun Context.findWindow(): Window? =
    when (this) {
        is Activity -> window
        is ContextWrapper -> baseContext.findWindow()
        else -> null
    }

@Composable
fun DynamicTheme(
    window: Window? = null,
    avoidSystemBar: Boolean = false,
    content: @Composable () -> Unit
) {
    DynamicThemeNoContent(window = window) {
        Surface(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
                .apply {
                    if (avoidSystemBar) {
                        this.systemBarsPadding()
                    }
                }
        ) {
            content()
        }
    }
}

@Composable
fun DynamicThemeNoContent(
    window: Window?=null,
    content: @Composable () -> Unit
) {
    val innerWindow =
        window ?: LocalContext.current.findWindow() ?: throw Exception("no find window")
    ThemeSettingsProvider() {
        WindowCompat.setDecorFitsSystemWindows(innerWindow, false)
        val isDark = LocalDarkThemePrefs.current.isDarkTheme()
        rememberSystemUiController(innerWindow).setSystemBarsColor(Color.Transparent, !isDark)

        val colorScheme = LocalColorScheme.current
        ProvideTextStyle(
            value = LocalTextStyle.current.copy(
                lineBreak = LineBreak.Paragraph,
                textDirection = TextDirection.Content
            )
        ) {
            MaterialTheme(
                colorScheme = colorScheme,
                typography = Typography,
            ) {
                content()
            }
        }
    }
}


@Composable
fun Activity.DynamicTheme(
    avoidSystemBar: Boolean = false,
    content: @Composable () -> Unit
) {
    DynamicTheme(window = window, avoidSystemBar = avoidSystemBar, content = content)
}

@Composable
fun Activity.DynamicThemeNoContent(
    content: @Composable () -> Unit
) {
    DynamicThemeNoContent(window = window, content = content)
}

@Composable
fun PreviewThemeLight(
    color: Color = Color(ThemeHelper.DEFAULT_COLOR_SEED),
    content: @Composable () -> Unit
) {
    val colorsScheme = mDynamicColorScheme(color, false, PaletteStyle.values()[0], 0.0)
    MaterialTheme(
        colorScheme = colorsScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}