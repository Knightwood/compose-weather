package com.kiylx.weather.ui.page.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.kiylx.weather.R
import com.kiylx.weather.common.AUnit
import com.kiylx.weather.common.AllPrefs
import com.kiylx.weather.icon.WeatherIconNoRound
import com.kiylx.weather.repo.bean.DailyEntity
import com.kiylx.weather.repo.bean.Location

class DailyWeatherHeaderPage {
}

/**
 * weather daily page header info
 */
@Composable
fun DailyWeatherHeaderPage(location: Location, state: State<DailyEntity?>) {
    state.value?.let { data ->
        //location text
        val locationText = if (location.default && AllPrefs.gpsAuto) {
            "${location.lat},${location.lon}"
        } else {
            "${location.adm1},${location.adm2}"
        }
        val unit = if (AllPrefs.unit == AUnit.MetricUnits.param) {
            "℃"
        } else {
            "℉"
        }
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 32.dp)
        ) {
            //顶部信息
            Column(verticalArrangement = Arrangement.Center) {
                Text(
                    text = locationText,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(16.dp, 4.dp)
                )
                //icon and weather info
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(32.dp, 8.dp)
                        .fillMaxWidth()
                ) {
                    WeatherIconNoRound(
                        data.data.icon.toInt(),
                        Modifier.align(Alignment.CenterVertically)
                    )
                    Text(
                        text = data.data.temp,
                        style = MaterialTheme.typography.displayLarge
                    )
                    Text(
                        text = unit,
                        style = MaterialTheme.typography.displayMedium
                    )
                }
                Text(
                    text = "${data.data.text} " +
                            "${stringResource(id = R.string.feels_like_str)}: ${data.data.feelsLike} $unit",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${stringResource(id = R.string.relative_humidity)}: ${data.data.humidity} %",
                        modifier = Modifier.align(Alignment.Bottom).padding(horizontal = 8.dp),
                        style = MaterialTheme.typography.labelMedium,
                    )
                    //wind
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterVertically).padding(horizontal = 8.dp),
                        text = "${stringResource(id = R.string.wind_direction)}: ${data.data.windDir} \n" +
                                "${stringResource(id = R.string.wind_rating)}: ${data.data.windScale} \n" +
                                "${stringResource(id = R.string.wind_speed)}: ${data.data.windSpeed} ${
                                    stringResource(
                                        id = R.string.wind_speed_unit
                                    )
                                }",
                        style = MaterialTheme.typography.labelMedium,
                    )
                }

            }
        }
    }

}
