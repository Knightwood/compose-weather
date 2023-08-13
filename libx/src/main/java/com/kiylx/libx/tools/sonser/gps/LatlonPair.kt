package com.kiylx.libx.tools.sonser.gps

import android.location.Location
import com.kiylx.libx.tools.sonser.gps.convert.LocationUtils

/**
 * @param latTitle 北纬，南纬
 * @param latValue 格式化的纬度坐标值（取绝对值、转换成度角分之类的）
 * @param lonTitle 东经，西经
 * @param lonValue 格式化的经度坐标值（取绝对值、转换成度角分之类的）
 */
data class LatlonPair(
    var latTitle: String = "", var latValue: String = "0",
    var lonTitle: String = "", var lonValue: String = "0"
) {
    companion object {
        /**
         * @param mode true:转换成度角分，false：不做转换
         */
        fun convert(latlon: Location, mode: Boolean = false): LatlonPair {
            val result = LatlonPair()
            if (latlon.latitude < 0) {
                result.latTitle = "南纬:"
            } else {
                result.latTitle = "北纬:"
            }
            if (latlon.longitude < 0) {
                result.lonTitle = "西经:"
            } else {
                result.lonTitle = "东经:"
            }
            if (!mode) {
                result.lonValue = String.format("%.5f", Math.abs(latlon.longitude))
                result.latValue = String.format("%.5f", Math.abs(latlon.latitude))
            } else {
                result.lonValue = LocationUtils.changeToDFM(latlon.longitude)
                result.latValue = LocationUtils.changeToDFM(latlon.latitude)
            }
            return result
        }

    }
}