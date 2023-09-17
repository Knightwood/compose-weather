package com.kiylx.weather.common

class UnitText {
}
fun unit(): String {
    val unit = if (AllPrefs.unit == AUnit.MetricUnits.param) {
        "℃"
    } else {
        "℉"
    }
    return unit
}