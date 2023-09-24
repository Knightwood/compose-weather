package com.kiylx.weather.ui.page.main

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
import androidx.compose.material.icons.filled.Info
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
import com.kiylx.weather.repo.bean.HourWeatherEntity
import com.kiylx.weather.repo.bean.IndicesEntity
import com.kiylx.weather.repo.bean.OneDayWeather
import com.kiylx.weather.repo.bean.WarningEntity
import com.kiylx.weather.ui.page.component.TitleCard
import kotlinx.coroutines.CoroutineScope
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.function.Function
import java.util.stream.Collectors


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyWeatherBodyPage(stateHolder: WeatherPagerStateHolder) {
    val dailyState: State<DailyEntity> = stateHolder.dailyUiState.asDataFlow().collectAsState()
    val todayIndicesState = stateHolder.todayIndicesData.asDataFlow().collectAsState()
    val todayHourWeatherState = stateHolder.dailyHourUiState.asDataFlow().collectAsState()
    val warningNowState = stateHolder.warningNowUiState.asDataFlow().collectAsState()
    val minutelyPrecipitationState =
        stateHolder.minutelyPrecipitationState.asDataFlow().collectAsState()
    // 空气质量信息和杂项
    val toDayWeatherState = stateHolder.threeDayWeatherData.asDataFlow().collectAsState()
    val toDayWeather = toDayWeatherState.value.data[0]

    LaunchedEffect(key1 = Unit) {
        stateHolder.getTodayIndices() //天气指数
        stateHolder.getDailyHourWeatherData() //逐小时预报
        stateHolder.getWarningNow()//天气预警
//        stateHolder.getMinutelyPrecipitation()//降水预报
    }
    val scope = rememberCoroutineScope()
    val warnBottomSheetHolder by remember {
        mutableStateOf(MBottomSheetHolder())
    }
    Surface {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            //天气预警
            if (warningNowState.value.data.isNotEmpty()) {
                WarningBar(warnBottomSheetHolder, warningNowState)
            }
            if (minutelyPrecipitationState.value.data.isNotEmpty()) {
                TitleCard(
                    icon = Icons.Filled.Info,
                    title = minutelyPrecipitationState.value.summary
                ) {
                    //柱状图
//                    val data = minutelyPrecipitationState.value.data.mapIndexed {index,it->
//                       val str= if (index==0){
//                            "当前"
//                        }else if (index ==minutelyPrecipitationState.value.data.size/2){
//                            "一小时后"
//                       }else{
//                           "两小时后"
//                       }
//
//                    }

                }
            }
            // 当前的天气变化横向列表
            Today24HourWeather(todayHourWeatherState)
            // 三天的天气状况列表
            ThreeDayWeather(stateHolder = stateHolder)
            //日出日落,月升月落等
            SunAndMoonAndOther(toDayWeather, dailyState)
            //天气指数
            DayIndices(todayIndicesState)

        }
    }
    //天气预警BottomSheet
    WarningBottomSheet(warnBottomSheetHolder, scope, warningNowState)
}

@Composable
private fun WarningBar(
    warnBottomSheetHolder: MBottomSheetHolder,
    warningNowState: State<WarningEntity>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
            .background(
                MaterialTheme.colorScheme.errorContainer,
                RoundedCornerShape(8.dp)
            )
            .clickable {
                warnBottomSheetHolder.show()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier
                .padding(8.dp)
                .weight(1f)
        )
        Text(
            text = warningNowState.value.data[0].title,
            modifier = Modifier
                .padding(4.dp)
                .weight(5f),
            style = MaterialTheme.typography.titleMedium,
        )
        Icon(
            imageVector = Icons.Filled.ArrowCircleRight,
            contentDescription = null,
            modifier = Modifier
                .padding(8.dp)
                .weight(1f),
        )
    }
}


/**
 * 24H的天气状况
 */
@Composable
private fun Today24HourWeather(todayHourWeatherState: State<HourWeatherEntity>) {
    val data = todayHourWeatherState.value.data
    val unit = tempUnit()

    TitleCard(
        icon = Icons.Filled.AccessTimeFilled,
        title = stringResource(R.string.hour_24_report)
    ) {
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
}

/**
 * 日出日落,月升月落等
 */
@Composable
private fun SunAndMoonAndOther(
    toDayWeather: OneDayWeather,
    dailyState: State<DailyEntity>
) {
    // 日出日落
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
}

/**
 * 天气指数
 */
@Composable
private fun DayIndices(todayIndicesState: State<IndicesEntity>) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 16.dp),
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
                    )
                    IconText(
                        modifier = Modifier.weight(1f),
                        title = stringResource(id = R.string.wash_car_index),
                        icon = painterResource(id = com.kiylx.weather.icon.R.drawable.wash_car),
                        iconSize = 38.dp,
                        text = indiectMap["2"]?.category ?: "",
                    )
                }
                Row {
                    IconText(
                        modifier = Modifier.weight(1f),
                        title = stringResource(id = R.string.dress_index),
                        icon = painterResource(id = com.kiylx.weather.icon.R.drawable.wear_dress),
                        iconSize = 38.dp,
                        text = indiectMap["3"]?.category ?: "",
                    )
                    IconText(
                        modifier = Modifier.weight(1f),
                        title = stringResource(id = R.string.cold_index),
                        icon = painterResource(id = com.kiylx.weather.icon.R.drawable.cold),
                        iconSize = 38.dp,
                        text = indiectMap["9"]?.category ?: "",
                    )
                }
            }
        }
    }
}

@Composable
private fun WarningBottomSheet(
    warnBottomSheetHolder: MBottomSheetHolder,
    scope: CoroutineScope,
    warningNowState: State<WarningEntity>
) {
    MBottomSheet(
        sheetHolder = warnBottomSheetHolder,
        scope = scope
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            items(warningNowState.value.data.size) {
                val warningNote = warningNowState.value.data[it]
                Surface {
                    Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp)) {
                        Row {
                            WeatherIcon(code = warningNote.type.toInt())
                            Text(
                                text = warningNote.title,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        Text(
                            text = warningNote.text,
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                        )
                        val pubTime = LocalDateTime.parse(
                            warningNote.pubTime,
                            DateTimeFormatter.ISO_OFFSET_DATE_TIME
                        ).format(LocalDateUtil.ymdhmsFormatter)
                        Text(
                            text = pubTime, modifier = Modifier
                                .align(Alignment.End)
                                .padding(4.dp)
                        )
                    }
                }
            }
            item {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp), horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            warnBottomSheetHolder.hide(scope)
                        }
                    ) {
                        Text(stringResource(id = com.kiylx.compose_lib.R.string.confirm))
                    }
                }
            }
        }
    }
}
