package com.kiylx.weather.ui.page

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddLocation
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.*
import com.kiylx.libx.http.kotlin.basic3.DataUiState
import com.kiylx.libx.http.kotlin.basic3.UiState
import com.kiylx.weather.common.AUnit
import com.kiylx.weather.common.AllPrefs
import com.kiylx.weather.icon.IconText
import com.kiylx.weather.icon.WeatherIcon
import com.kiylx.weather.icon.WeatherIconNoRound
import com.kiylx.weather.repo.QWeatherGeoRepo
import com.kiylx.weather.repo.bean.DailyEntity
import com.kiylx.weather.repo.bean.Location
import com.kiylx.weather.ui.activitys.MainViewModel
import com.loren.component.view.composesmartrefresh.MyRefreshHeader
import com.loren.component.view.composesmartrefresh.SmartSwipeRefresh
import com.loren.component.view.composesmartrefresh.SmartSwipeStateFlag
import com.loren.component.view.composesmartrefresh.rememberSmartSwipeRefreshState
import java.time.LocalDate
import com.kiylx.weather.R

class MainPage {
    companion object {
        val TAG = "主页"
    }
}

/**
 * 主页
 */
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun MainPage(
    viewModel: MainViewModel,
    navigateToSettings: () -> Unit,
    navigateToLocations: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp),
        ) {
            IconButton(
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.TopStart),
                onClick = {
                    navigateToLocations()
                }) {
                Icon(Icons.Rounded.AddLocation, contentDescription = "定位")
            }
            IconButton(
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.TopEnd),
                onClick = {
                    navigateToSettings()
                }) {
                Icon(Icons.Rounded.Settings, contentDescription = "定位")
            }
        }
        //根据地点数量显示pager页面
        val allLocations = QWeatherGeoRepo.allLocationsFlow.collectAsState()
        PagerFragment(allLocations, viewModel)
    }
}

/**
 * 水平的pager
 * 根据位置数量，显示多个可水平滑动切换，显示天气状况的pager
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerFragment(locations: State<MutableList<Location>>, viewModel: MainViewModel) {
    val pagerState = rememberPagerState() {
        locations.value.size
    }
    HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) {
        Surface(modifier = Modifier.fillMaxSize()) {
            MainPagePager(location = QWeatherGeoRepo.allLocations[it], it, viewModel)
        }
    }
}

/**
 * 集成下拉刷新，获取天气状况的方法，也就是单个的pager，内部持有真的显示天气状况的页面
 * @param index 0即默认位置，如果开启了gps更新，就得定位刷新
 *
 */
@Composable
fun MainPagePager(location: Location, index: Int, viewModel: MainViewModel) {
    val data = remember { DataUiState<DailyEntity>() }
    LaunchedEffect(key1 = location, block = {
        viewModel.getDailyData(data, location)
    })
    val pageData = data.asDataFlow().collectAsState()//页面数据

    val uiState = data.asUiStateFlow().collectAsState()
    UiStateToastMsg(state = uiState)//界面状态toast消息

    //下拉刷新
    val refreshState = rememberSmartSwipeRefreshState()
    SmartSwipeRefresh(
        modifier = Modifier.fillMaxSize(),
        onRefresh = {
            viewModel.getDailyData(data, location)
        },
        state = refreshState,
        isNeedRefresh = true,
        isNeedLoadMore = false,
        headerIndicator = {
            MyRefreshHeader(refreshState.refreshFlag, true)
        },
    ) {
        //观察界面状态，修改下拉刷新状态
        LaunchedEffect(uiState.value) {
            uiState.value.let {

                when (it) {
                    is UiState.Success<*> -> {
                        refreshState.refreshFlag = SmartSwipeStateFlag.SUCCESS
                    }

                    is UiState.RequestErr,
                    is UiState.OtherErr -> {
                        refreshState.refreshFlag = SmartSwipeStateFlag.ERROR
                    }

                    UiState.Loading -> {}
                    else -> {
                        refreshState.refreshFlag = SmartSwipeStateFlag.IDLE
                    }
                }
            }
        }
        //只有可滑动的时候，下载刷新才能生效，所以，这里配置了垂直滑动
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            MainPageContent(location = location, state = pageData)
        }
    }
}

/**
 * weather info content
 */
@Composable
fun MainPageContent(location: Location, state: State<DailyEntity?>) {
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
        val nowTimeText = LocalDate.now().format(AllPrefs.dateFormatter)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Surface(modifier = Modifier.padding(bottom = 32.dp)) {
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
                }
            }
            //上面是大概的信息
            //下面是其他数据,每行都是双列
            //tab切换页
            DailyWeatherInfo(location = location, dailyEntity = data)
        }
    }

}

class DayWeatherType {
    companion object {
        const val threeDayWeather = 1
        const val sevenDayWeather = 2
    }
}

@Composable
fun ColumnScope.DailyWeatherInfo(location: Location, dailyEntity: DailyEntity) {
    Row(modifier =Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween) {
        IconText(
            icon = painterResource(WeatherIcon.getResId()),
            title = "title",
            text = "text body",
        )
        IconText(
            icon = painterResource(WeatherIcon.getResId()),
            title = "title",
            text = "text body"
        )
    }
}

/**
 * get three or seven day weather and show info
 */
@Composable
fun DayWeather(location: Location, type: Int = DayWeatherType.threeDayWeather) {

}