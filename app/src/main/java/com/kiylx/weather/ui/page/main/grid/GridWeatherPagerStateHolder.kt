package com.kiylx.weather.ui.page.main.grid

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.kiylx.libx.http.kotlin.basic3.flow.DataUiState
import com.kiylx.weather.http.sendRequest
import com.kiylx.weather.repo.QWeatherRepo
import com.kiylx.weather.repo.bean.DailyEntity
import com.kiylx.weather.repo.bean.DayWeather
import com.kiylx.weather.repo.bean.HourWeatherEntity
import com.kiylx.weather.repo.bean.LocationEntity
import com.kiylx.weather.ui.page.main.DayWeatherType

/**
 * 格点天气
 */
class GridWeatherPagerStateHolder(location: LocationEntity) {
    //位置信息
    val location: MutableState<LocationEntity> = mutableStateOf(location)

    //================================当天的===============================
    //实时天气
    val dailyUiState: DataUiState<DailyEntity> = DataUiState(DailyEntity(updateTime = "-1"))

    //  逐小时预报
    val dailyHourUiState: DataUiState<HourWeatherEntity> = DataUiState(HourWeatherEntity())

    //三天的天气
    val threeDayWeatherData: DataUiState<DayWeather> = DataUiState(DayWeather())

    //================================未来===============================
    //七天的天气
    val sevenDayWeatherData: DataUiState<DayWeather> = DataUiState(DayWeather())


    /**
     * get weather info and update UiState
     */
    suspend fun getDailyData(noCache: Boolean = false) {
        dailyUiState.sendRequest {
            QWeatherRepo.getDailyReport_Grid(location = location.value, noCache = noCache)
        }
    }

    /**
     * 获取每小时天气
     */
    suspend fun getDailyHourWeatherData(noCache: Boolean = false) {
        dailyHourUiState.sendRequest {
            QWeatherRepo.getDailyHourReport_Grid(location.value, noCache = noCache)
        }
    }

    suspend fun getDayWeatherData(type: Int = DayWeatherType.threeDayWeather) {
        val dayUiData = when (type) {
            DayWeatherType.threeDayWeather -> threeDayWeatherData
            DayWeatherType.sevenDayWeather -> sevenDayWeatherData
            else -> throw IllegalArgumentException("illegal type")
        }

        dayUiData.sendRequest {
            QWeatherRepo.getDayReport_Grid(location.value, type)
        }
    }

}