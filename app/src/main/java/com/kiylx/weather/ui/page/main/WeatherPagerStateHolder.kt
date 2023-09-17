package com.kiylx.weather.ui.page.main

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.kiylx.libx.http.kotlin.basic3.DataUiState
import com.kiylx.libx.http.kotlin.basic3.UiState
import com.kiylx.libx.http.kotlin.common.RawResponse
import com.kiylx.weather.repo.QWeatherRepo
import com.kiylx.weather.repo.bean.DailyEntity
import com.kiylx.weather.repo.bean.DayAirEntity
import com.kiylx.weather.repo.bean.DayWeather
import com.kiylx.weather.repo.bean.Location

class WeatherPagerStateHolder(location: Location) {
    //位置信息
    val location: MutableState<Location> = mutableStateOf(location)

    //================================当天的===============================
    //实时天气
    val dailyUiState: DataUiState<DailyEntity> = DataUiState(DailyEntity())

    // todo 逐小时预报
    // 5天的空气质量
    val fiveDayAirData :DataUiState<DayAirEntity> = DataUiState(DayAirEntity())
    // todo 天气预警
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
        dailyUiState.setUiState(UiState.Loading)
        when (val response = QWeatherRepo.getDailyReport(location.value, noCache = noCache)) {
            is RawResponse.Error -> {
                dailyUiState.setUiState(UiState.RequestErr(response))
            }

            is RawResponse.Success -> {
                if (response.responseData?.code == "200") {
                    dailyUiState.setDataOrState(
                        response.responseData,
                        UiState.Success(response.responseData)
                    )
                } else {
                    dailyUiState.setUiState(
                        UiState.OtherErr(
                            response.responseData?.code?.toInt(),
                        )
                    )
                }
            }
        }
    }

    suspend fun getDayWeatherData(type: Int = DayWeatherType.threeDayWeather) {
        val dayUiData = when (type) {
            DayWeatherType.threeDayWeather -> threeDayWeatherData
            DayWeatherType.sevenDayWeather -> sevenDayWeatherData
            DayWeatherType.fifteenDayWeather -> fifteenDayWeatherData
            else -> throw IllegalArgumentException("illegal type")
        }

        dayUiData.setUiState(UiState.Loading)
        when (val response = QWeatherRepo.getDayReport(location.value, type)) {
            is RawResponse.Error -> {
                dayUiData.setUiState(UiState.RequestErr(response))
            }

            is RawResponse.Success -> {
                if (response.responseData?.code == "200") {
                    dayUiData.setDataOrState(
                        response.responseData,
                        UiState.Success(response.responseData)
                    )
                } else {
                    dayUiData.setUiState(
                        UiState.OtherErr(
                            response.responseData?.code?.toInt(),
                        )
                    )
                }
            }
        }
    }

    /**
     * 获取5天的空气质量
     */
   suspend fun get5DAir() {
        fiveDayAirData.setUiState(UiState.Loading)
        when (val response = QWeatherRepo.getDayAir(location.value)) {
            is RawResponse.Error -> {
                fiveDayAirData.setUiState(UiState.RequestErr(response))
            }

            is RawResponse.Success -> {
                if (response.responseData?.code == "200") {
                    fiveDayAirData.setDataOrState(
                        response.responseData,
                        UiState.Success(response.responseData)
                    )
                } else {
                    fiveDayAirData.setUiState(
                        UiState.OtherErr(
                            response.responseData?.code?.toInt(),
                        )
                    )
                }
            }
        }
    }

}