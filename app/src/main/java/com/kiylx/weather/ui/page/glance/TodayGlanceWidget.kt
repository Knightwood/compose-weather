package com.kiylx.weather.ui.page.glance

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalGlanceId
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.kiylx.compose_lib.theme3.DarkThemePrefs
import com.kiylx.compose_lib.theme3.ThemeHelper
import com.kiylx.libx.http.kotlin.basic3.flow.DataUiState
import com.kiylx.libx.tools.LocalDateUtil
import com.kiylx.weather.R
import com.kiylx.weather.common.tempUnit
import com.kiylx.weather.icon.WeatherIcon
import com.kiylx.weather.repo.QWeatherGeoRepo
import com.kiylx.weather.repo.bean.DailyEntity
import com.kiylx.weather.repo.bean.DayWeather
import com.kiylx.weather.repo.bean.HourWeatherEntity
import com.kiylx.weather.repo.bean.LocationEntity
import com.kiylx.weather.ui.activitys.MainActivity
import com.kiylx.weather.ui.page.main.DayWeatherType
import com.kiylx.weather.ui.page.main.WeatherPagerStateHolder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object TodayGlanceRepo {
    val weatherHolder = WeatherPagerStateHolder(QWeatherGeoRepo.allLocationState[0])
}

/**
 * Number of cells, n cells(width) * m cells(height)
 * Available size in portrait mode (dp) : (73n - 16) x (118m - 16)
 * Available size in landscape mode (dp) : (142n - 15) x (66m - 15)
 */
class TodayGlanceWidget : GlanceAppWidget() {
    val TAG = "TodayGlanceWidget"
    val weatherHolder = TodayGlanceRepo.weatherHolder

    override val sizeMode: SizeMode = SizeMode.Responsive(
        setOf(SMALL_SQUARE_1, SMALL_SQUARE_2, HORIZONTAL_RECTANGLE_1, HORIZONTAL_RECTANGLE_2)
    )

    override suspend fun onDelete(context: Context, glanceId: GlanceId) {
        super.onDelete(context, glanceId)
        TodayGlanceUpdateWorker.cancel(context, glanceId)
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val isDark = if (ThemeHelper.darkThemeMode == DarkThemePrefs.FOLLOW_SYSTEM) {
                //isSystemInDarkTheme()//这里会报错，原因未知
                false
            } else {
                ThemeHelper.darkThemeMode == DarkThemePrefs.ON
            }
            GlanceTheme(colors = ThemeColorProvider.getColors(isDark)) {
                val glanceId = LocalGlanceId.current
                SideEffect {
                    TodayGlanceUpdateWorker.enqueue(context, glanceId)
                }
                when (LocalSize.current) {
                    SMALL_SQUARE_1,
                    SMALL_SQUARE_2 -> {
                        LaunchedEffect(key1 = Unit) {
                            weatherHolder.getDailyData()
                            weatherHolder.getDayWeatherData(DayWeatherType.threeDayWeather)
                        }
                        Content_2x2(
                            weatherHolder.dailyUiState,
                            weatherHolder.threeDayWeatherData,
                            weatherHolder.location
                        )
                    }

                    HORIZONTAL_RECTANGLE_1,
                    HORIZONTAL_RECTANGLE_2 -> {
                        LaunchedEffect(key1 = Unit, block = {
                            weatherHolder.getDailyData()
                            weatherHolder.getDayWeatherData(DayWeatherType.threeDayWeather)
                            weatherHolder.getDailyHourWeatherData()
                        })
                        Content_4x2(
                            weatherHolder.dailyUiState,
                            weatherHolder.dailyHourUiState,
                            weatherHolder.threeDayWeatherData,
                            weatherHolder.location
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun Content_2x2(
        dailyUiState: DataUiState<DailyEntity>,
        threeDayWeatherData: DataUiState<DayWeather>,
        location: MutableState<LocationEntity>
    ) {
        val todayData = dailyUiState.asDataFlow().collectAsState()
        val threeData = threeDayWeatherData.asDataFlow().collectAsState()

        Column(
            modifier = GlanceModifier.fillMaxSize().padding(16.dp)
                .background(GlanceTheme.colors.secondaryContainer)
                .cornerRadius(28.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Row(
                modifier = GlanceModifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = GlanceModifier.size(46.dp),
                    provider = ImageProvider(WeatherIcon.getResId(todayData.value.data.icon.toInt())),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(GlanceTheme.colors.secondary)
                )
                Spacer(modifier = GlanceModifier.defaultWeight())
                Column {
                    Text(
                        modifier = GlanceModifier,
                        text = location.value.adm2,
                        style = TextStyle(
                            color = GlanceTheme.colors.tertiary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                        )
                    )
                    Text(
                        modifier = GlanceModifier,
                        text = location.value.name,
                        style = TextStyle(
                            color = GlanceTheme.colors.secondary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    )
                }

            }
            Row(
                modifier = GlanceModifier.fillMaxWidth().padding(top = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    modifier = GlanceModifier,
                    verticalAlignment = Alignment.Vertical.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = GlanceModifier.padding(4.dp),
                        text = todayData.value.data.temp + tempUnit(),
                        style = TextStyle(
                            color = GlanceTheme.colors.secondary,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    )
                    val today = threeData.value.data[0]
                    Text(
                        text = "${today.tempMin}${tempUnit()}/${today.tempMax}${tempUnit()}",
                        modifier = GlanceModifier.padding(4.dp),
                        style = TextStyle(
                            color = GlanceTheme.colors.secondary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                        )
                    )
                }
            }
        }
    }

    /**
     * 4x2 and 4x3
     */
    @Composable
    private fun Content_4x2(
        dailyUiState: DataUiState<DailyEntity>,
        dailyHourUiState: DataUiState<HourWeatherEntity>,
        threeDayWeatherData: DataUiState<DayWeather>,
        location: MutableState<LocationEntity>
    ) {
        val todayData = dailyUiState.asDataFlow().collectAsState()
        val threeData = threeDayWeatherData.asDataFlow().collectAsState()
        val hourData = dailyHourUiState.asDataFlow().collectAsState()

        // Size will be one of the sizes defined above.
        val size = LocalSize.current
        Column(
            modifier = GlanceModifier.fillMaxSize().padding(16.dp)
                .background(GlanceTheme.colors.secondaryContainer)
                .cornerRadius(28.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Row(
                modifier = GlanceModifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = GlanceModifier.size(46.dp),
                    provider = ImageProvider(WeatherIcon.getResId(todayData.value.data.icon.toInt())),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(GlanceTheme.colors.secondary)
                )
                Spacer(modifier = GlanceModifier.defaultWeight())
                Column {
                    Text(
                        modifier = GlanceModifier,
                        text = location.value.adm2,
                        style = TextStyle(
                            color = GlanceTheme.colors.tertiary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                        )
                    )
                    Text(
                        modifier = GlanceModifier,
                        text = location.value.name,
                        style = TextStyle(
                            color = GlanceTheme.colors.secondary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    )
                }

            }
            Row(
                modifier = GlanceModifier.fillMaxWidth().padding(top = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    modifier = GlanceModifier,
                    verticalAlignment = Alignment.Vertical.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = GlanceModifier.padding(4.dp),
                        text = todayData.value.data.temp + tempUnit(),
                        style = TextStyle(
                            color = GlanceTheme.colors.secondary,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    )
                    val today = threeData.value.data[0]
                    Text(
                        text = "${today.tempMin}${tempUnit()}/${today.tempMax}${tempUnit()}",
                        modifier = GlanceModifier.padding(4.dp),
                        style = TextStyle(
                            color = GlanceTheme.colors.secondary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                        )
                    )
                }
                Spacer(modifier = GlanceModifier.defaultWeight())
                Row(modifier = GlanceModifier) {
                    val hourWeatherList = hourData.value.data
                    repeat(if (hourWeatherList.size > 4) 4 else hourWeatherList.size) {
                        Column(
                            modifier = GlanceModifier.padding(start = 8.dp),
                            verticalAlignment = Alignment.Vertical.CenterVertically,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val hourTmp = hourWeatherList[it]
                            Text(
                                text = hourTmp.temp + tempUnit(),
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = GlanceTheme.colors.tertiary,
                                ),
                            )
                            Image(
                                modifier = GlanceModifier.size(40.dp)
                                    .padding(top = 8.dp, bottom = 8.dp),
                                provider = ImageProvider(WeatherIcon.getResId(hourTmp.icon.toInt())),
                                colorFilter = ColorFilter.tint(GlanceTheme.colors.secondary),
                                contentDescription = null
                            )
                            Text(
                                text = LocalDateTime.parse(
                                    hourTmp.fxTime,
                                    DateTimeFormatter.ISO_OFFSET_DATE_TIME
                                ).toLocalTime()
                                    .format(LocalDateUtil.hmFormatter),
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = GlanceTheme.colors.tertiary,
                                )
                            )
                        }
                    }
                }
            }

            //three day weather
            if (size.height >= HORIZONTAL_RECTANGLE_2.height) {
                Column(
                    modifier = GlanceModifier
                        .background(GlanceTheme.colors.secondaryContainer)
                        .fillMaxWidth().padding(top = 16.dp)
                        .cornerRadius(22.dp),
                ) {
                    val threeDayList = threeData.value.data
                    val todayLocalDate = LocalDate.now()
                    val nowTime = LocalTime.now()
                    repeat(threeDayList.size) {
                        val oneDayWeather = threeDayList[it]
                        Row(modifier = GlanceModifier.fillMaxWidth()) {
                            val fxDate = LocalDate.parse(oneDayWeather.fxDate)
                            val dateStr = if (fxDate.isEqual(todayLocalDate)) {
                                LocalContext.current.getString(R.string.today)
                            } else if (fxDate.minusDays(1).isEqual(todayLocalDate)) {
                                LocalContext.current.getString(R.string.tomorrow)
                            } else {
                                fxDate.format(LocalDateUtil.mdFormatter)
                            }
                            Text(
                                modifier = GlanceModifier,
                                text = dateStr,
                                style = TextStyle(
                                    color = GlanceTheme.colors.secondary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                )
                            )
                            Spacer(modifier = GlanceModifier.defaultWeight())
                            Row(
                                modifier = GlanceModifier.padding(bottom = 4.dp),
                                verticalAlignment = Alignment.Vertical.CenterVertically,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val iconCode = if (nowTime.hour in 6..18) {
                                    oneDayWeather.iconDay.toInt()
                                } else {
                                    oneDayWeather.iconNight.toInt()
                                }
                                Image(
                                    modifier = GlanceModifier.size(36.dp),
                                    provider = ImageProvider(WeatherIcon.getResId(iconCode)),
                                    colorFilter = ColorFilter.tint(GlanceTheme.colors.secondary),
                                    contentDescription = null
                                )
                                Text(
                                    modifier = GlanceModifier.padding(start = 8.dp),
                                    text = oneDayWeather.tempMin + tempUnit(),
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        color = GlanceTheme.colors.tertiary,
                                    ),
                                )
                                Text(
                                    modifier = GlanceModifier.padding(start = 8.dp),
                                    text = oneDayWeather.tempMax + tempUnit(),
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        color = GlanceTheme.colors.tertiary,
                                    ),
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun navToMainActivity() {
        actionStartActivity<MainActivity>()
    }

    companion object {
        //small to large: 2x1 2x2 4x2 4x3
        private val SMALL_SQUARE_1 = DpSize(110.dp, 50.dp)
        private val SMALL_SQUARE_2 = DpSize(110.dp, 110.dp)
        private val HORIZONTAL_RECTANGLE_1 = DpSize(250.dp, 110.dp)
        private val HORIZONTAL_RECTANGLE_2 = DpSize(250.dp, 220.dp)
    }
}