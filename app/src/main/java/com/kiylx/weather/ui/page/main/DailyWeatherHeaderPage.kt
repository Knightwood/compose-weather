package com.kiylx.weather.ui.page.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kiylx.weather.R
import com.kiylx.weather.common.AllPrefs
import com.kiylx.weather.common.Route
import com.kiylx.weather.common.WindUnit
import com.kiylx.weather.common.tempUnit
import com.kiylx.weather.icon.WeatherIconNoRound
import com.kiylx.weather.repo.bean.DailyEntity
import com.kiylx.weather.ui.activitys.LocalNavController


/**
 * weather daily page header info
 */
@Composable
fun DailyWeatherHeaderPage(state: State<DailyEntity>) {
    state.value.let { data ->
        val navController = LocalNavController.current
        val unit = tempUnit()
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp, top = 18.dp)
        ) {
            //顶部信息
            Column(verticalArrangement = Arrangement.Center) {
                //icon and weather info
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .fillMaxWidth()
                ) {
                    Column {
                        //天气图标
                        WeatherIconNoRound(
                            code = data.data.icon.toInt(),
                            iconSize = 90.dp,
                        )
                        //温度
                        Text(
                            text = data.data.temp + unit,
                            style = MaterialTheme.typography.displayMedium
                        )
                    }

                    VerticalDivider(modifier = Modifier.fillMaxHeight(), thickness = 2.dp)
                    Column {

                        Text(
                            text = data.data.text,
                            style = MaterialTheme.typography.displayLarge
                        )

                        //体感温度
                        Text(
                            text = "${stringResource(id = R.string.feels_like_str)}: ${data.data.feelsLike}$unit",
                            style = MaterialTheme.typography.titleLarge,
                        )
                        Text(
                            text = "${stringResource(id = R.string.relative_humidity)}: ${data.data.humidity}%",
                            modifier = Modifier.padding(vertical = 4.dp),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }

                }
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "${stringResource(id = R.string.vis)}: ${data.data.vis}km",
                        modifier = Modifier.padding(end = 8.dp),
                        style = MaterialTheme.typography.labelMedium,
                    )
                    val windSpeed = if (AllPrefs.windUnit == WindUnit.Km) {
                        "${data.data.windSpeed} ${
                            stringResource(
                                id = R.string.wind_speed_unit
                            )
                        }"
                    } else {
                        "${data.data.windScale} ${
                            stringResource(
                                id = R.string.wind_rating_unit
                            )
                        }"
                    }
                    Text(
                        text = "${data.data.windDir}: $windSpeed",
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
                if (AllPrefs.gridWeather && AllPrefs.gpsAuto) {
                    //点击前往格点天气
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier
                            .padding(end=16.dp)
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    RoundedCornerShape(28.dp)
                                )
                                .clickable {
                                    navController.navigate(Route.GRID_WEATHER)
                                }
                                .padding(horizontal = 8.dp, vertical = 8.dp)

                        ) {
                            Icon(imageVector = Icons.Filled.Info, contentDescription = null)
                            Text(text = "格点天气")
                        }
                    }
                }

                //todo 空气质量
            }
        }
    }

}
