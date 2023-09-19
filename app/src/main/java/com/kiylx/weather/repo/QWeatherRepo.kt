package com.kiylx.weather.repo

import com.kiylx.libx.http.kotlin.basic3.handleApi3
import com.kiylx.libx.http.kotlin.common.RawResponse
import com.kiylx.libx.http.kotlin.common.Retrofit2Holder
import com.kiylx.weather.common.AllPrefs
import com.kiylx.weather.http.minutesToSeconds
import com.kiylx.weather.repo.api.Api
import com.kiylx.weather.repo.bean.DailyAirEntity
import com.kiylx.weather.repo.bean.DailyEntity
import com.kiylx.weather.repo.bean.DayAirEntity
import com.kiylx.weather.repo.bean.DayWeather
import com.kiylx.weather.repo.bean.HourWeatherEntity
import com.kiylx.weather.repo.bean.IndicesEntity
import com.kiylx.weather.repo.bean.Location
import com.kiylx.weather.repo.bean.Location.Companion.toLatLonStr
import com.kiylx.weather.repo.bean.WarningEntity
import com.kiylx.weather.ui.page.main.DayWeatherType

/**
 * 调用和风天气api，并在这里提供数据
 */
object QWeatherRepo {

    private val api by lazy {
        Retrofit2Holder(AllPrefs.baseUrl).create(Api::class.java)
    }

//<editor-fold desc="网络接口">
    /**
     * 获取实时天气
     * 会查询本地副本
     */
    suspend fun getDailyReport(
        location: Location,
        unit: String = AllPrefs.unit,
        lang: String = AllPrefs.lang,
        noCache: Boolean = false
    ): RawResponse<DailyEntity> {
        val cacheTime = if (noCache) null else AllPrefs.dailyInterval.minutesToSeconds()
        val res = if (location.default && AllPrefs.gpsAuto) {
            //默认位置，需要使用经纬度获取数据
            handleApi3(api.getGridDaily(location.toLatLonStr(), lang, unit, cacheTime))
        } else {
            handleApi3(api.getDaily(location.id, lang, unit, cacheTime))
        }
        return res
    }

    /**
     * 获取小时天气
     * 会查询本地副本
     */
    suspend fun getDailyHourReport(
        location: Location,
        unit: String = AllPrefs.unit,
        lang: String = AllPrefs.lang,
        noCache: Boolean = false
    ): RawResponse<HourWeatherEntity> {
        val cacheTime = if (noCache) null else AllPrefs.hourWeatherInterval.minutesToSeconds()
        val res = handleApi3(api.getHourWeather(location.toLatLonStr(), lang, unit, cacheTime))
        return res
    }
    /**
     * 获取天气预警
     * 会查询本地副本
     */
    suspend fun getWarningNow(
        location: Location,
        lang: String = AllPrefs.lang,
        noCache: Boolean = false
    ): RawResponse<WarningEntity> {
        val cacheTime = if (noCache) null else AllPrefs.earlyWarningInterval.minutesToSeconds()
        val res=handleApi3(api.getWarningNow(location.toLatLonStr(), lang, cacheTime))
        return res
    }

    /**
     * 获取未来天气状况
     * 会查询本地副本
     */
    suspend fun getDayReport(
        location: Location,
        type: Int = DayWeatherType.threeDayWeather,
        unit: String = AllPrefs.unit,
        lang: String = AllPrefs.lang,
        noCache: Boolean = false
    ): RawResponse<DayWeather> {
        val cacheTime = if (noCache) null else AllPrefs.dayWeatherInterval.minutesToSeconds()
        val res = handleApi3(
            when (type) {
                DayWeatherType.threeDayWeather -> api.getDayWeather3d(
                    location.toLatLonStr(),
                    lang,
                    unit,
                    cacheTime
                )

                DayWeatherType.sevenDayWeather -> api.getDayWeather7d(
                    location.toLatLonStr(),
                    lang,
                    unit,
                    cacheTime
                )

                DayWeatherType.fifteenDayWeather -> api.getDayWeather15d(
                    location.toLatLonStr(),
                    lang,
                    unit,
                    cacheTime
                )

                else -> throw IllegalArgumentException("illegal type")
            }
        )
        return res
    }


    /**
     * 获取实时空气质量
     * 会查询本地副本
     */
    suspend fun getDailyAir(
        location: Location,
        lang: String = AllPrefs.lang,
        noCache: Boolean = false
    ): RawResponse<DailyAirEntity> {
        val cacheTime = if (noCache) null else AllPrefs.weatherAirInterval.minutesToSeconds()
        val res = handleApi3(api.getDailyAir(location.toLatLonStr(), lang, cacheTime))
        return res
    }

    /**
     * 获取未来5天空气质量
     * 会查询本地副本
     */
    suspend fun getDayAir(
        location: Location,
        lang: String = AllPrefs.lang,
        noCache: Boolean = false
    ): RawResponse<DayAirEntity> {
        val cacheTime = if (noCache) null else AllPrefs.weatherAirDayInterval.minutesToSeconds()
        val res = handleApi3(api.getDayAir(location.toLatLonStr(), lang, cacheTime))
        return res
    }


    /**
     * 获取当天的天气指数
     * 会查询本地副本
     */
    suspend fun getIndices1d(
        location: Location,
        type: String,
        lang: String = AllPrefs.lang,
        noCache: Boolean = false
    ): RawResponse<IndicesEntity> {
        val cacheTime = if (noCache) null else AllPrefs.weatherIndicesInterval.minutesToSeconds()
        val res = handleApi3(api.getIndices1d(location.toLatLonStr(), lang, type, cacheTime))
        return res
    }


//</editor-fold>

}