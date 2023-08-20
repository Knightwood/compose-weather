package com.kiylx.weather.common

import com.kiylx.libx.mmkv.boolM
import com.kiylx.libx.mmkv.intM
import com.kiylx.libx.mmkv.longM
import com.kiylx.libx.mmkv.strM
import com.tencent.mmkv.MMKV

object AllPrefs {
    val mk = MMKV.defaultMMKV()

//
//<editor-fold desc="SDK设置">
    //=======================sdk==================
    var payPlan by mk.intM(PrefKeyName.play_plan, PayPlan.free)

    var apiKey by mk.strM(
        PrefKeyName.api_key, "88e87e5caa7a4e84a42d9df1dba236db"
    )

    /**
     * 地理位置baseurl
     */
    val geoBaseUrl="https://geoapi.qweather.com/"

    /**
     * 天气的baseurl
     */
    val baseUrl: String
        get() {
            return if (payPlan == PayPlan.free) {
                "https://devapi.qweather.com/"
            } else{
                "https://api.qweather.com/"
            }
        }

//</editor-fold>
//<editor-fold desc="偏好">
    /**
     * 标记是否第一次进入应用
     */
    var firstEnter by mk.boolM("firstEnter",true)

    /**
     * 是否打开gps实时更新默认地点
     */
    var gpsAuto by mk.boolM("gpsAuto",false)

    /**
     * 是否使用天气信息缓存
     */
    var useWeatherCache by mk.boolM("useWeatherCache",true)

//</editor-fold>
//<editor-fold desc="天气数据更新缓存时间周期">
    /**
     *实时天气缓存时长，单位：分钟
     */
    var dailyInterval by mk.longM("dailyInterval", 30)

    /**
     *逐小时预报缓存时长，单位：分钟
     */
    var hourWeatherInterval by mk.longM("hourWeatherInterval", 60)

    /**
     * 逐天天气预报 单位：分钟
     */
    var dayWeatherInterval by mk.longM("dayWeatherInterval", 2*60)

    /**
     * 天气预警 单位：分钟
     */
    var earlyWarningInterval by mk.longM("earlyWarningInterval", 10)

    /**
     * 天气指数 单位：分钟
     */
    var weatherIndicesInterval by mk.longM("weatherIndicesInterval", 3*60)
    /**
     * 分钟降水 单位：分钟
     */
    var weatherMinutelyInterval by mk.longM("weatherMinutelyInterval", 5)

    /**
     * 实时空气质量 单位：分钟
     */
    var weatherAirInterval by mk.longM("weatherAirInterval", 30)

    /**
     * 逐天空气质量 单位：分钟
     */
    var weatherAirDayInterval by mk.longM("weatherAirDayInterval", 60*3)

//</editor-fold>

}

/**
 * 付费方式
 */
class PayPlan {
    companion object {
        const val free = 1
        const val payPlan = 2
    }
}

class PrefKeyName{
    companion object{
        const val api_key: String = "api_key"
        const val play_plan: String = "play_plan"
    }
}
