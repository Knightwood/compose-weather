package com.kiylx.weather.ui.page.main

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
fun DailyWeatherInfo(stateHolder: WeatherPagerStateHolder) {
    val dailyState: State<DailyEntity> = stateHolder.dailyUiState.asDataFlow().collectAsState()
    val todayIndicesState = stateHolder.todayIndicesData.asDataFlow().collectAsState()
    val todayHourWeatherState = stateHolder.dailyHourUiState.asDataFlow().collectAsState()

    LaunchedEffect(key1 = Unit) {
        stateHolder.getTodayIndices() //天气指数
        stateHolder.getDailyHourWeatherData() //逐小时预报
    }

    Surface {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // todo 实时天气预警卡片
            // 当前的天气变化横向列表
            val data = todayHourWeatherState.value.data
            val unit = tempUnit()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(
                        MaterialTheme.colorScheme.tertiaryContainer,
                        RoundedCornerShape(8.dp)
                    )
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(data.size) {
                    //这里的surface很宽，会把上面的内容覆盖，所以上面的背景色设置会看起来像是没有生效
                    Surface {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 4.dp, vertical = 8.dp)
                                .heightIn(min = 120.dp)
                                .background(
                                    MaterialTheme.colorScheme.secondaryContainer,
                                    RoundedCornerShape(8.dp)
                                ),
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
                            WeatherIcon(code = data[it].icon.toInt(), iconSize = 24.dp)
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

            // 三天的天气状况列表
            DayWeather(stateHolder = stateHolder)
            // 空气质量信息和杂项
            val toDayWeatherState = stateHolder.threeDayWeatherData.asDataFlow().collectAsState()
            val toDayWeather = toDayWeatherState.value.data[0]
            //日出日落
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconText(
                    title = stringResource(id = R.string.sunrise),
                    icon = painterResource(id = com.kiylx.weather.icon.R.drawable.sun_rise),
                    text = toDayWeather.sunrise,
                )
                IconText(
                    title = stringResource(id = R.string.sunset),
                    icon = painterResource(id = com.kiylx.weather.icon.R.drawable.sun_set),
                    text = toDayWeather.sunset,
                )
            }

            //月相，月初月落
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TwoText(
                    title = stringResource(id = R.string.moonrise),
                    text = toDayWeather.moonrise,
                )
                IconText(
                    title = stringResource(id = R.string.moonPhase),
                    padding = PaddingValues(horizontal = 0.dp, vertical = 4.dp),
                    icon = painterResource(id = WeatherIcon.getResId(toDayWeather.moonPhaseIcon.toInt())),
                    text = toDayWeather.moonPhase
                )
                TwoText(
                    title = stringResource(id = R.string.moonset),
                    text = toDayWeather.moonset,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconText(
                    title = stringResource(id = R.string.pressure),
                    icon = painterResource(id = com.kiylx.weather.icon.R.drawable.pressure),
                    iconSize = 38.dp,
                    text = dailyState.value.data.pressure + " 百帕",
                )
                IconText(
                    modifier = Modifier.weight(1f),
                    title = stringResource(id = R.string.uvIndex),
                    icon = painterResource(id = com.kiylx.weather.icon.R.drawable.ultraviolet),
                    iconSize = 40.dp,
                    text = toDayWeather.uvIndex
                )

            }
//            Row {
//                IconText(
//                    title = stringResource(id = R.string.vis),
//                    text = dailyState.value.data.vis + " 公里",
//                    icon = painterResource(id = com.kiylx.weather.icon.R.drawable.vis),
//                )
//            }

            //天气指数
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
            ) {//运动指数1 洗车指数2 穿衣指数3 感冒指数9
                val indiectMap = todayIndicesState.value.data.stream().collect(
                    Collectors.toMap(
                        IndicesEntity.Daily::type,
                        Function.identity()
                    )
                )

                Card() {
                    Column {
                        Row {
                            IconText(
                                modifier = Modifier.weight(1f),
                                title = stringResource(id = R.string.sportIndex),
                                icon = painterResource(id = com.kiylx.weather.icon.R.drawable.sport),
                                iconSize = 38.dp,
                                text = indiectMap["1"]?.category ?: "",
                                backgroundColor = Color.Transparent
                            )
                            IconText(
                                modifier = Modifier.weight(1f),
                                title = stringResource(id = R.string.wash_car_index),
                                icon = painterResource(id = com.kiylx.weather.icon.R.drawable.wash_car),
                                iconSize = 38.dp,
                                text = indiectMap["2"]?.category ?: "",
                                backgroundColor = Color.Transparent
                            )
                        }
                        Row {
                            IconText(
                                modifier = Modifier.weight(1f),
                                title = stringResource(id = R.string.dress_index),
                                icon = painterResource(id = com.kiylx.weather.icon.R.drawable.wear_dress),
                                iconSize = 38.dp,
                                text = indiectMap["3"]?.category ?: "",
                                backgroundColor = Color.Transparent

                            )
                            IconText(
                                modifier = Modifier.weight(1f),
                                title = stringResource(id = R.string.cold_index),
                                icon = painterResource(id = com.kiylx.weather.icon.R.drawable.cold),
                                iconSize = 38.dp,
                                text = indiectMap["9"]?.category ?: "",
                                backgroundColor = Color.Transparent
                            )
                        }
                    }

                }
            }

        }
    }

}
