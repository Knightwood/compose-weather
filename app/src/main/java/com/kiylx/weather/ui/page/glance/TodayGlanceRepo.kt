package com.kiylx.weather.ui.page.glance

import com.kiylx.libx.http.kotlin.common.RawResponse
import com.kiylx.weather.repo.QWeatherGeoRepo
import com.kiylx.weather.repo.QWeatherRepo
import com.kiylx.weather.ui.page.main.DayWeatherType

object TodayGlanceRepo {

    /**
     * request network get new info
     */
    suspend fun getInfo(): WeatherInfo {
        val location = QWeatherGeoRepo.allLocationState[0]
        val dailyReport = QWeatherRepo.getDailyReport(location = location)
        val dayReport = QWeatherRepo.getDayReport(location, DayWeatherType.threeDayWeather)
        val dailyHourReport = QWeatherRepo.getDailyHourReport(location)
        if (dailyReport is RawResponse.Success &&
            dayReport is RawResponse.Success &&
            dailyHourReport is RawResponse.Success
        ) {
            return WeatherInfo.Available(
                location = location,
                currentData = dailyReport.responseData!!,
                hourlyForecast = dailyHourReport.responseData!!.data,
                dayForecast = dayReport.responseData!!.data
                )
        } else {
            return WeatherInfo.Unavailable("error")
        }
    }
}