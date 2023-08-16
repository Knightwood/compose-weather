package com.kiylx.weather.repo

import com.kiylx.weather.repo.bean.DailyEntity
import com.kiylx.weather.repo.bean.Location
import kotlinx.serialization.Serializable

/**
 * 持有某个位置的所有天气状况
 */
@Serializable
data class WeatherSub(
    val location: Location,
    val dailyEntity: DailyEntity
)