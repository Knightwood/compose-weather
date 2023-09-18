package com.kiylx.weather.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.kiylx.weather.R

class UnitText {
}
fun tempUnit(): String {
    val unit = if (AllPrefs.unit == AUnit.MetricUnits.param) {
        "℃"
    } else {
        "℉"
    }
    return unit
}

@Composable
fun windUnit (type :Int):String{
    val unit = if (type == WindUnit.Km) {
        stringResource(id = R.string.wind_speed_unit)
    } else {
        stringResource(id = R.string.wind_rating_unit)
    }
    return unit
}