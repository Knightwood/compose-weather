package com.kiylx.weather.ui.page.main.grid

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.kiylx.weather.repo.QWeatherRepo
import com.kiylx.weather.repo.bean.DailyEntity
import com.kiylx.weather.repo.bean.DayWeather
import com.kiylx.weather.repo.bean.HourWeatherEntity
import com.kiylx.weather.repo.bean.LocationEntity
import com.kiylx.weather.ui.page.main.DayWeatherType
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * 格点天气
 */
class GridWeatherPagerStateHolder(location: LocationEntity) {
    //位置信息
    val location: MutableState<LocationEntity> = mutableStateOf(location)

    //================================当天的===============================
    //实时天气
    val dailyUiState: MutableStateFlow<DailyEntity> = MutableStateFlow(DailyEntity(updateTime = "-1"))

    //  逐小时预报
    val dailyHourUiState: MutableStateFlow<HourWeatherEntity> = MutableStateFlow(HourWeatherEntity())

    //三天的天气
    val threeDayWeatherData: MutableStateFlow<DayWeather> = MutableStateFlow(DayWeather())

    //================================未来===============================
    //七天的天气
    val sevenDayWeatherData: MutableStateFlow<DayWeather> = MutableStateFlow(DayWeather())


    /**
     * get weather info and update UiState
     */
    suspend fun getDailyData(noCache: Boolean = false) {
        dailyUiState.emit(
            QWeatherRepo.getDailyReport_Grid(location = location.value, noCache = noCache)
        )
    }

    /**
     * 获取每小时天气
     */
    suspend fun getDailyHourWeatherData(noCache: Boolean = false) {
        dailyHourUiState.emit(
            QWeatherRepo.getDailyHourReport_Grid(location.value, noCache = noCache)
        )
    }

    suspend fun getDayWeatherData(type: Int = DayWeatherType.threeDayWeather) {
        val dayUiData = when (type) {
            DayWeatherType.threeDayWeather -> threeDayWeatherData
            DayWeatherType.sevenDayWeather -> sevenDayWeatherData
            else -> throw IllegalArgumentException("illegal type")
        }

        dayUiData.emit(QWeatherRepo.getDayReport_Grid(location.value, type))
    }

}