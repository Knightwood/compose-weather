package com.kiylx.weather.ui.page.main

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.kiylx.weather.common.AllPrefs
import com.kiylx.weather.common.Lang
import com.kiylx.weather.repo.QWeatherRepo
import com.kiylx.weather.repo.bean.DailyEntity
import com.kiylx.weather.repo.bean.DayAirEntity
import com.kiylx.weather.repo.bean.DayWeather
import com.kiylx.weather.repo.bean.HourWeatherEntity
import com.kiylx.weather.repo.bean.IndicesEntity
import com.kiylx.weather.repo.bean.LocationEntity
import com.kiylx.weather.repo.bean.MinutelyPrecipitationEntity
import com.kiylx.weather.repo.bean.WarningEntity
import kotlinx.coroutines.flow.MutableStateFlow

class WeatherPagerStateHolder(location: LocationEntity) {
    //位置信息
    val location: MutableState<LocationEntity> = mutableStateOf(location)

    //================================当天的===============================
    //实时天气
    val dailyUiState: MutableStateFlow<DailyEntity> =
        MutableStateFlow(DailyEntity(updateTime = "-1"))

    //  逐小时预报
    val dailyHourUiState: MutableStateFlow<HourWeatherEntity> =
        MutableStateFlow(HourWeatherEntity())

    //  分钟级降水
    val minutelyPrecipitationState: MutableStateFlow<MinutelyPrecipitationEntity> =
        MutableStateFlow(MinutelyPrecipitationEntity())

    // 是否有雨
    val showRainLineChart = mutableStateOf(false)

    //天气预警 -当前
    val warningNowUiState: MutableStateFlow<WarningEntity> = MutableStateFlow(WarningEntity())

    //当天的天气指数
    val todayIndicesData: MutableStateFlow<IndicesEntity> = MutableStateFlow(IndicesEntity())

    // 5天的空气质量
    val fiveDayAirData: MutableStateFlow<DayAirEntity> = MutableStateFlow(DayAirEntity())

    //三天的天气
    val threeDayWeatherData: MutableStateFlow<DayWeather> = MutableStateFlow(DayWeather())

    //================================未来===============================
    //七天的天气
    val sevenDayWeatherData: MutableStateFlow<DayWeather> = MutableStateFlow(DayWeather())

    //十五天的天气
    val fifteenDayWeatherData: MutableStateFlow<DayWeather> = MutableStateFlow(DayWeather())

    /**
     * get weather info and update UiState
     */
    suspend fun getDailyData(noCache: Boolean = false) {
        dailyUiState.emit(QWeatherRepo.getDailyReport(location = location.value, noCache = noCache))
    }

    /**
     * 获取每小时天气
     */
    suspend fun getDailyHourWeatherData(noCache: Boolean = false) {
        dailyHourUiState.emit(
            QWeatherRepo.getDailyHourReport(location.value, noCache = noCache)
        )
    }

    /**
     * 获取分钟级降水
     */
    suspend fun getMinutelyPrecipitation(noCache: Boolean = false) {
        //todo 如果无雨雪，就不查询
//        if (dailyUiState.getData().data.text)
        minutelyPrecipitationState.emit(
            QWeatherRepo.getMinutelyPrecipitation(
                location.value,
                noCache = noCache,
                lang = if (AllPrefs.lang == Lang.Chinese.param) "zh" else "en"
            )
        )
    }

    suspend fun getDayWeatherData(type: Int = DayWeatherType.threeDayWeather) {
        val dayUiData = when (type) {
            DayWeatherType.threeDayWeather -> threeDayWeatherData
            DayWeatherType.sevenDayWeather -> sevenDayWeatherData
            DayWeatherType.fifteenDayWeather -> fifteenDayWeatherData
            else -> throw IllegalArgumentException("illegal type")
        }

        dayUiData.emit(
            QWeatherRepo.getDayReport(location.value, type)
        )
    }

    /**
     * 获取5天的空气质量
     */
    suspend fun get5DAir() {
        fiveDayAirData.emit(
            QWeatherRepo.getDayAir(location.value)
        )
    }

    /**
     * 获取当天的天气指数
     */
    suspend fun getTodayIndices() {
        todayIndicesData.emit(
            QWeatherRepo.getIndices1d(
                location.value,
                "1,2,3,9"
            )
        )
    }

    /**
     * get weather warning
     */
    suspend fun getWarningNow() {
        warningNowUiState.emit(QWeatherRepo.getWarningNow(location.value))
    }

    suspend fun refresh() {
        getDailyData(noCache = true)
        getDailyHourWeatherData(noCache = true)
        getMinutelyPrecipitation(noCache = true)
        getDayWeatherData(DayWeatherType.threeDayWeather)
        getDayWeatherData(DayWeatherType.sevenDayWeather)
        get5DAir()
        getTodayIndices()
        getWarningNow()
    }

}