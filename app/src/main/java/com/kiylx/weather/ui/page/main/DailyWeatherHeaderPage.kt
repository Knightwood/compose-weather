package com.kiylx.weather.ui.page.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kiylx.weather.R
import com.kiylx.weather.common.AllPrefs
import com.kiylx.weather.common.WindUnit
import com.kiylx.weather.common.tempUnit
import com.kiylx.weather.icon.WeatherIconNoRound
import com.kiylx.weather.repo.bean.DailyEntity
import com.kiylx.weather.repo.bean.LocationEntity

class DailyWeatherHeaderPage {
}

/**
 * weather daily page header info
 */
@Composable
fun DailyWeatherHeaderPage(location: LocationEntity, state: State<DailyEntity>) {
    state.value.let { data ->
        val unit = tempUnit()
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp)
        ) {
            //顶部信息
            Column(verticalArrangement = Arrangement.Center) {
                //icon and weather info
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(32.dp, 8.dp)
                        .fillMaxWidth()
                ) {
                    WeatherIconNoRound(
                        code = data.data.icon.toInt(),
                        iconSize = 52.dp,
                        modifier = Modifier.align(Alignment.CenterVertically)
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
                    Column(
                        modifier = Modifier
                            .align(Alignment.Bottom)
                            .padding(horizontal = 12.dp)
                    ) {
                        Text(
                            text = "${stringResource(id = R.string.relative_humidity)}: ${data.data.humidity} %",
                            modifier = Modifier.padding(vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                        )
                        Text(
                            text = "${stringResource(id = R.string.vis)}: ${data.data.vis} 公里",
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }

                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(horizontal = 12.dp)
                    ) {
                        //wind
                        Text(
                            text = "${stringResource(id = R.string.wind_direction)}: ${data.data.windDir} ",
                            style = MaterialTheme.typography.labelMedium,
                        )
                        if (AllPrefs.windUnit == WindUnit.Km) {
                            Text(
                                text = "${stringResource(id = R.string.wind_speed)}: ${data.data.windSpeed} ${
                                    stringResource(
                                        id = R.string.wind_speed_unit
                                    )
                                }",
                                style = MaterialTheme.typography.labelMedium,
                            )
                        } else {
                            Text(
                                text = "${stringResource(id = R.string.wind_rating)}: ${data.data.windScale} ${
                                    stringResource(
                                        id = R.string.wind_rating_unit
                                    )
                                }",
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                    }

                }

            }
        }
    }

}
