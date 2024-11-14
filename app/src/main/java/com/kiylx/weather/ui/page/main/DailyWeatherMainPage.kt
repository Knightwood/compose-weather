package com.kiylx.weather.ui.page.main

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddLocation
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kiylx.weather.R
import com.kiylx.weather.repo.QWeatherGeoRepo
import com.kiylx.weather.repo.bean.LocationEntity
import com.kiylx.weather.repo.bean.LocationEntity.Companion.toLatLonStr
import com.kiylx.weather.ui.activitys.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainPage {
    companion object {
        val TAG = "tty1-主页"
    }
}

/**
 * 主页
 */
@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun DailyWeatherMainPage(
    viewModel: MainViewModel,
    navigateToSettings: () -> Unit,
    navigateToLocations: () -> Unit,
) {
    //根据地点数量显示pager页面
    val locationData = QWeatherGeoRepo.allLocationState

    val pagerState = rememberPagerState() {
        locationData.size
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Surface {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    modifier = Modifier
                        .size(48.dp),
                    onClick = {
                        navigateToLocations()
                    }) {
                    Icon(Icons.Rounded.AddLocation, contentDescription = "定位")
                }
                if (locationData.size > 0) {
                    var index = pagerState.currentPage
                    if (index >= locationData.size) {
                        index = locationData.size - 1
                    }
                    LocationText(location = locationData[index])
                }
                IconButton(
                    modifier = Modifier
                        .size(48.dp),
                    onClick = {
                        navigateToSettings()
                    }) {
                    Icon(Icons.Rounded.Settings, contentDescription = "设置")
                }
            }

        }
        //下面的不同位置的天气页面pager
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) {
            Surface(modifier = Modifier.fillMaxSize()) {
                val weatherPagerStateHolder = remember {
                    mutableStateOf(viewModel.getWeatherStateHolder(locationData[it]))
                }
                MainPagePager(weatherPagerStateHolder.value, it)
            }
        }
    }
}

@Composable
fun LocationText(location: LocationEntity) {
    //location text
    val locationText =
        "${location.adm2},${location.name}"
    Text(
        text = locationText,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier
            .padding(16.dp, 8.dp)
    )
}

@Composable
fun LatLonText(location: LocationEntity, modifier: Modifier = Modifier) {
    //location text
    val locationText =
        location.toLatLonStr()
    Text(
        text = locationText,
        style = MaterialTheme.typography.titleMedium,
        modifier = modifier
            .padding(16.dp, 8.dp)
    )
}

/**
 * 集成下拉刷新，获取天气状况的方法，也就是单个的pager，内部持有真的显示天气状况的页面
 *
 * @param index 0即默认位置，如果开启了gps更新，就得定位刷新
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPagePager(weatherPagerStateHolder: WeatherPagerStateHolder, index: Int) {
    //当天的天气状况
    val uiState = weatherPagerStateHolder.dailyUiState.asUiStateFlow().collectAsState()
    LaunchedEffect(key1 = Unit, block = {
        weatherPagerStateHolder.getDailyData()
    })
    val scope = rememberCoroutineScope()
    var isRefreshing by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            weatherPagerStateHolder.run {
                refresh()
            }
        }
    }
    PullToRefreshBox(
        modifier = Modifier.fillMaxSize(),
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            scope.launch {
                delay(1000)
                isRefreshing = false
            }
        }
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //顶部信息
            DailyWeatherHeaderPage(weatherPagerStateHolder)
            //tab切换页
            var tabIndex by remember {
                mutableIntStateOf(0)
            }
            val tabs = listOf(
                stringResource(id = R.string.today),
                stringResource(R.string.seven_days_in_the_future)
            )
            TabRow(
                selectedTabIndex = tabIndex,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                indicator = { tabPositions ->
                    TabRowDefaults.PrimaryIndicator(
                        Modifier
                            .tabIndicatorOffset(tabPositions[tabIndex])
                            .height(4.dp),
                    )
                }
            ) {
                tabs.forEachIndexed { index, name ->
                    Tab(
                        selected = tabIndex == index,
                        onClick = { tabIndex = index },
                        modifier = Modifier
                            .padding(horizontal = 4.dp, vertical = 8.dp)
                            .background(
                                color = MaterialTheme.colorScheme.background,
                                RoundedCornerShape(8.dp)
                            )
                            .heightIn(min = 36.dp),
                    ) {
                        Text(name)
                    }
                }
            }
            when (tabIndex) {
                0 -> {
                    DailyWeatherBodyPage(stateHolder = weatherPagerStateHolder)
                }

                1 -> {
                    MultiDayWeatherPage(
                        stateHolder = weatherPagerStateHolder,
                        type = DayWeatherType.sevenDayWeather
                    )
                }

                2 -> {
                    MultiDayWeatherPage(
                        stateHolder = weatherPagerStateHolder,
                        type = DayWeatherType.fifteenDayWeather
                    )
                }
            }
            Text(text = stringResource(R.string.data_from_hefeng), color = Color.Gray)
        }
    }
}


class DayWeatherType {
    companion object {
        const val threeDayWeather = 1
        const val sevenDayWeather = 2
        const val fifteenDayWeather = 3
    }
}