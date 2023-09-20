package com.kiylx.weather.ui.page.main

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.kiylx.libx.http.kotlin.basic3.flow.DataUiState
import com.kiylx.weather.http.sendRequest
import com.kiylx.weather.repo.QWeatherRepo
import com.kiylx.weather.repo.bean.DailyEntity
import com.kiylx.weather.repo.bean.DayAirEntity
import com.kiylx.weather.repo.bean.DayWeather
import com.kiylx.weather.repo.bean.HourWeatherEntity
import com.kiylx.weather.repo.bean.IndicesEntity
import com.kiylx.weather.repo.bean.LocationEntity
import com.kiylx.weather.repo.bean.WarningEntity

class WeatherPagerStateHolder(location: LocationEntity) {
    //位置信息
    val location: MutableState<LocationEntity> = mutableStateOf(location)

    //================================当天的===============================
    //实时天气
    val dailyUiState: DataUiState<DailyEntity> = DataUiState(DailyEntity())

    //  逐小时预报
    val dailyHourUiState: DataUiState<HourWeatherEntity> = DataUiState(HourWeatherEntity())

    //天气预警 -当前
    val warningNowUiState: DataUiState<WarningEntity> = DataUiState(WarningEntity())

    //当天的天气指数
    val todayIndicesData: DataUiState<IndicesEntity> = DataUiState(IndicesEntity())

    // 5天的空气质量
    val fiveDayAirData: DataUiState<DayAirEntity> = DataUiState(DayAirEntity())

    //三天的天气
    val threeDayWeatherData: DataUiState<DayWeather> = DataUiState(DayWeather())

    //================================未来===============================
    //七天的天气
    val sevenDayWeatherData: DataUiState<DayWeather> = DataUiState(DayWeather())

    //十五天的天气
    val fifteenDayWeatherData: DataUiState<DayWeather> = DataUiState(DayWeather())

    /**
     * get weather info and update UiState
     */
    suspend fun getDailyData(noCache: Boolean = false) {
        dailyUiState.sendRequest {
            QWeatherRepo.getDailyReport(location = location.value, noCache = noCache)
        }
    }

    /**
     * 获取每小时天气
     */
    suspend fun getDailyHourWeatherData(noCache: Boolean = false) {
        dailyHourUiState.sendRequest {
            QWeatherRepo.getDailyHourReport(location.value, noCache = noCache)
        }
    }

    suspend fun getDayWeatherData(type: Int = DayWeatherType.threeDayWeather) {
        val dayUiData = when (type) {
            DayWeatherType.threeDayWeather -> threeDayWeatherData
            DayWeatherType.sevenDayWeather -> sevenDayWeatherData
            DayWeatherType.fifteenDayWeather -> fifteenDayWeatherData
            else -> throw IllegalArgumentException("illegal type")
        }

        dayUiData.sendRequest {
            QWeatherRepo.getDayReport(location.value, type)
        }
    }

    /**
     * 获取5天的空气质量
     */
    suspend fun get5DAir() {
        fiveDayAirData.sendRequest {
            QWeatherRepo.getDayAir(location.value)
        }
    }

    /**
     * 获取当天的天气指数
     */
    suspend fun getTodayIndices() {
        todayIndicesData.sendRequest {
            QWeatherRepo.getIndices1d(
                location.value,
                "1,2,3,9"
            )
        }
    }

    /**
     * get weather warning
     */
    suspend fun getWarningNow() {
        warningNowUiState.sendRequest {
            QWeatherRepo.getWarningNow(
                location.value,
            )
        }
    }

}