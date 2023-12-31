package com.kiylx.weather.common

import com.kiylx.libx.mmkv.boolM
import com.kiylx.libx.mmkv.intM
import com.kiylx.libx.mmkv.longM
import com.kiylx.libx.mmkv.strM
import com.tencent.mmkv.MMKV
import java.time.format.DateTimeFormatter

object AllPrefs {
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

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
    val geoBaseUrl = "https://geoapi.qweather.com/"

    /**
     * 天气的baseurl
     */
    val baseUrl: String
        get() {
            return if (payPlan == PayPlan.free) {
                "https://devapi.qweather.com/"
            } else {
                "https://api.qweather.com/"
            }
        }

//</editor-fold>
//<editor-fold desc="偏好">
    /**
     * 标记是否第一次进入应用
     */
    var firstEnter by mk.boolM("firstEnter", true)

    /**
     * 是否打开gps实时更新默认地点
     */
    var gpsAuto by mk.boolM("gpsAuto", false)

    /**
     * 是否查询格点天气
     * 前提是gpsAuto需要为true
     */
    var gridWeather by mk.boolM("gridWeather",false)

//</editor-fold>
//<editor-fold desc="天气数据更新缓存时间周期">
    /**
     *位置查找缓存时长，单位：分钟
     */
    var locationInterval by mk.longM("locationInterval", 600)

    /**
     *实时天气缓存时长，单位：分钟
     */
    var dailyInterval by mk.longM("dailyInterval", 20)

    /**
     *逐小时预报缓存时长，单位：分钟
     */
    var hourWeatherInterval by mk.longM("hourWeatherInterval", 30)

    /**
     * 逐天天气预报 单位：分钟
     */
    var dayWeatherInterval by mk.longM("dayWeatherInterval", 180)

    /**
     * 天气预警 单位：分钟
     */
    var earlyWarningInterval by mk.longM("earlyWarningInterval", 60)

    /**
     * 天气指数 单位：分钟
     */
    var weatherIndicesInterval by mk.longM("weatherIndicesInterval", 360)

    /**
     * 分钟降水 单位：分钟
     */
    var weatherMinutelyInterval by mk.longM("weatherMinutelyInterval", 15)

    /**
     * 实时空气质量 单位：分钟
     */
    var weatherAirInterval by mk.longM("weatherAirInterval", 30)

    /**
     * 逐天空气质量 单位：分钟
     */
    var weatherAirDayInterval by mk.longM("weatherAirDayInterval", 8 * 60)

//</editor-fold>

//<editor-fold desc="单位">
    /**
     * 单位
     */
    var unit by mk.strM("unit", AUnit.MetricUnits.param)

    /**
     * 语言
     */
    var lang by mk.strM("lang", Lang.Chinese.param)

    /**
     * 风力等级还是mk/h
     */
    var windUnit by mk.intM("wind_unit",WindUnit.Km)

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

class PrefKeyName {
    companion object {
        const val api_key: String = "api_key"
        const val play_plan: String = "play_plan"
    }
}

/**
 * 风力等级还是km/h
 */
class WindUnit {
    companion object {
        const val Km = 1
        const val BeaufortScale = 2
    }
}

/**
 * 默认采用公制单位，例如：公里、摄氏度等，
 * @param param 请求时所用参数
 * @param flag Android属性
 */
enum class AUnit(val param: String, val flag: String) {
    MetricUnits("m", "METRIC"),
    ImperialUnits("i", "IMPERIAL"),
}

/**
 * 语言设置
 */
enum class Lang(val param: String, val names: String, val flag: String) {
    Chinese("zh-hans", "简体中文", "ZH_HANS"),
    English("en", "英语", "ENGLISH"),
    Japanese("ja", "日本语", "JAPANESE")
}
