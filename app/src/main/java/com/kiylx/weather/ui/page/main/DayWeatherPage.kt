package com.kiylx.weather.ui.page.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kiylx.weather.repo.bean.Location

/**
 * get multi day weather and show info
 */
@Composable
fun DayWeather(location: Location, type: Int = DayWeatherType.sevenDayWeather) {
    Surface(modifier = Modifier.padding(horizontal = 8.dp)) {

    }
}