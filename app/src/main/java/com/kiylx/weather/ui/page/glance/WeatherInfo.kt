/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kiylx.weather.ui.page.glance

import com.kiylx.weather.repo.bean.DailyEntity
import com.kiylx.weather.repo.bean.HourWeatherEntity
import com.kiylx.weather.repo.bean.LocationEntity
import com.kiylx.weather.repo.bean.OneDayWeather
import kotlinx.serialization.Serializable

@Serializable
sealed interface WeatherInfo {
    @Serializable
    data object Loading : WeatherInfo

    @Serializable
    data class Available(
        val location: LocationEntity,
        val currentData: DailyEntity,
        val hourlyForecast: List<HourWeatherEntity.Hourly>,
        val dayForecast: List<OneDayWeather>
    ) : WeatherInfo

    @Serializable
    data class Unavailable(val message: String) : WeatherInfo
}