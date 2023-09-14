package com.kiylx.compose_lib.component

import android.util.Log
import androidx.annotation.CheckResult
import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

private const val TAG = "SVGString"

@CheckResult
fun String.parseDynamicColor(
    tonalPalettes: ColorScheme,
    isDarkTheme: Boolean
): String =
    replace("fill=\"(.+?)\"".toRegex()) {
        val value = it.groupValues[1]
        Log.i(TAG, "parseDynamicColor: $value")
        if (value.startsWith("#")) return@replace it.value
        runCatching {
            val (scheme, tone) = value.split("(?<=\\d)(?=\\D)|(?=\\d)(?<=\\D)".toRegex())
            val argb = when (scheme) {
                "p" -> tonalPalettes.primary
                "s" -> tonalPalettes.secondary
                "t" -> tonalPalettes.tertiary
                "n" -> tonalPalettes.surface
                "nv" -> tonalPalettes.surfaceVariant
                else -> Color.Transparent
            }.toArgb()
            "fill=\"${String.format("#%06X", 0xFFFFFF and argb)}\""
        }.getOrDefault(it.value)
    }


private fun String.autoDark(isDarkTheme: Boolean): Double =
    if (!isDarkTheme) this.toDouble()
    else when (this.toDouble()) {
        10.0 -> 99.0
        20.0 -> 95.0
        25.0 -> 90.0
        30.0 -> 90.0
        40.0 -> 80.0
        50.0 -> 60.0
        60.0 -> 50.0
        70.0 -> 40.0
        80.0 -> 40.0
        90.0 -> 30.0
        95.0 -> 20.0
        98.0 -> 10.0
        99.0 -> 10.0
        100.0 -> 20.0
        else -> this.toDouble()
    }
