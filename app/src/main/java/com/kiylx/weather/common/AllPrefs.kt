package com.kiylx.weather.common

import com.kiylx.libx.mmkv.boolM
import com.kiylx.libx.mmkv.intM
import com.kiylx.libx.mmkv.longM
import com.kiylx.libx.mmkv.strM
import com.tencent.mmkv.MMKV

object AllPrefs {

    val mmkv = MMKV.defaultMMKV()
    //=======================sdk==================
    var payPlan by mmkv.intM(PrefKeyName.play_plan, PayPlan.free)

    var apiKey by mmkv.strM(
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

    //===================偏好========================
    /**
     * 标记是否第一次进入应用
     */
        var firstEnter by mmkv.boolM("firstEnter",true)

    /**
     * 是否打开gps实时更新默认地点
     */
    var gpsAuto by mmkv.boolM("gps_auto",false)

    //==========================天气数据更新缓存时间周期==========================//
    /**
     *实时天气缓存时长，单位：分钟
     */
    var dailyInterval by mmkv.longM("dailyInterval", 30)

    /**
     *逐小时预报缓存时长，单位：分钟
     */
    var hourWeatherInterval by mmkv.longM("hourWeatherInterval", 60)

    /**
     * 逐天天气预报 单位：分钟
     */
    var dayWeatherInterval by mmkv.longM("dayWeatherInterval", 2*60)

    /**
     * 天气预警 单位：分钟
     */
    var earlyWarningInterval by mmkv.longM("earlyWarningInterval", 10)

    /**
     * 天气指数 单位：分钟
     */
    var weatherIndicesInterval by mmkv.longM("weatherIndicesInterval", 3*60)
    /**
     * 分钟降水 单位：分钟
     */
    var weatherMinutelyInterval by mmkv.longM("weatherMinutelyInterval", 5)

    /**
     * 实时空气质量 单位：分钟
     */
    var weatherAirInterval by mmkv.longM("weatherAirInterval", 30)

    /**
     * 空气质量逐天 单位：分钟
     */
    var weatherAirDayInterval by mmkv.longM("weatherAirDayInterval", 60*3)

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
