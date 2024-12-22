package com.kiylx.weather.ui.page.glance

import com.kiylx.weather.http.isOK
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
        if (dailyReport.isOK() && dayReport.isOK() && dailyHourReport.isOK()) {
            return WeatherInfo.Available(
                location = location,
                currentData = dailyReport,
                hourlyForecast = dailyHourReport.data,
                dayForecast = dayReport.data
            )
        } else {
            return WeatherInfo.Unavailable("error")
        }
    }
}