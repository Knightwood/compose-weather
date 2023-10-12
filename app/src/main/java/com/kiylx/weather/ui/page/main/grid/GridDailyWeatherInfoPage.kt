package com.kiylx.weather.ui.page.main.grid

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.ArrowCircleRight
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kiylx.compose_lib.component.MBottomSheet
import com.kiylx.compose_lib.component.MBottomSheetHolder
import com.kiylx.libx.tools.LocalDateUtil
import com.kiylx.weather.R
import com.kiylx.weather.common.AllPrefs
import com.kiylx.weather.common.WindUnit
import com.kiylx.weather.common.tempUnit
import com.kiylx.weather.common.windUnit
import com.kiylx.weather.icon.IconText
import com.kiylx.weather.icon.TwoText
import com.kiylx.weather.icon.WeatherIcon
import com.kiylx.weather.repo.bean.DailyEntity
import com.kiylx.weather.repo.bean.IndicesEntity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.function.Function
import java.util.stream.Collectors


@Composable
fun GridDailyWeatherInfo(stateHolder: GridWeatherPagerStateHolder) {
    val dailyState: State<DailyEntity> = stateHolder.dailyUiState.asDataFlow().collectAsState()
    val todayHourWeatherState = stateHolder.dailyHourUiState.asDataFlow().collectAsState()

    LaunchedEffect(key1 = Unit) {
        stateHolder.getDailyHourWeatherData() //逐小时预报
    }

    Surface {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // 当前的天气变化横向列表
            Card(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
            ) {
                val data = todayHourWeatherState.value.data
                val unit = tempUnit()
                Row(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Filled.AccessTimeFilled, contentDescription = null)
                    Text(
                        text = stringResource(R.string.hour_24_report),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(data.size) {
                        //这里的surface很宽，会把上面的内容覆盖，所以上面的背景色设置会看起来像是没有生效
                        Surface(color = Color.Transparent) {
                            Column(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                //weather info
                                Text(
                                    text = data[it].text,
                                    modifier = Modifier
                                        .padding(4.dp),
                                )
                                //temp
                                Text(
                                    text = "${data[it].temp}$unit",
                                )
                                //图标
                                WeatherIcon(
                                    code = data[it].icon.toInt(),
                                    iconSize = 24.dp,
                                    tint = MaterialTheme.colorScheme.primary,
                                    backgroundColor = MaterialTheme.colorScheme.primaryContainer
                                )
                                //time
                                Text(
                                    modifier = Modifier
                                        .padding(4.dp),
                                    text = LocalDateTime.parse(
                                        data[it].fxTime,
                                        DateTimeFormatter.ISO_OFFSET_DATE_TIME
                                    ).toLocalTime()
                                        .format(LocalDateUtil.hmFormatter)
                                )
                                val windText = if (AllPrefs.windUnit == WindUnit.Km) {
                                    data[it].windSpeed + windUnit(WindUnit.Km)
                                } else {
                                    data[it].windScale + windUnit(WindUnit.BeaufortScale)
                                }
                                Text(
                                    modifier = Modifier
                                        .padding(4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    text = data[it].windDir + "\n" + windText
                                )
                            }
                        }

                    }
                }
            }
            // 三天的天气状况列表
            GridDayWeather(stateHolder = stateHolder)
            // 空气质量信息和杂项
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconText(
                    modifier = Modifier.weight(1f),
                    title = stringResource(id = R.string.pressure),
                    icon = painterResource(id = com.kiylx.weather.icon.R.drawable.pressure),
                    iconSize = 40.dp,
                    text = dailyState.value.data.pressure + " 百帕",
                )
                IconText(
                    modifier = Modifier.weight(1f),
                    title = stringResource(id = R.string.dew),
                    icon = painterResource(id = com.kiylx.weather.icon.R.drawable.ultraviolet),
                    iconSize = 40.dp,
                    text = dailyState.value.data.dew
                )

            }
        }
    }
}
