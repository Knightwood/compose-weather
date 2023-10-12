package com.kiylx.weather.ui.page.main.grid

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kiylx.libx.http.kotlin.basic3.UiState
import com.kiylx.weather.R
import com.kiylx.weather.ui.page.component.CustomRefreshHeader
import com.kiylx.weather.ui.page.main.DayWeatherType
import com.loren.component.view.composesmartrefresh.SmartSwipeRefresh
import com.loren.component.view.composesmartrefresh.SmartSwipeStateFlag
import com.loren.component.view.composesmartrefresh.rememberSmartSwipeRefreshState

@Composable
fun GridWeatherPage(weatherPagerStateHolder: GridWeatherPagerStateHolder) {
    //位置信息
    val location = weatherPagerStateHolder.location.value
    //当天的天气状况
    val data = weatherPagerStateHolder.dailyUiState

    val pageData = data.asDataFlow().collectAsState()//页面数据

    val uiState = data.asUiStateFlow().collectAsState()
    LaunchedEffect(key1 = Unit, block = {
        weatherPagerStateHolder.getDailyData()
    })
    //下拉刷新
    val refreshState = rememberSmartSwipeRefreshState()
    SmartSwipeRefresh(
        modifier = Modifier.fillMaxSize().systemBarsPadding(),
        onRefresh = {
            weatherPagerStateHolder.run {
                getDailyData(noCache = true)
                getDailyHourWeatherData(noCache = true)
            }

            delay(3000L)
            if (refreshState.isRefreshing()) {
                refreshState.refreshFlag = if (uiState.value is UiState.Success<*>) {
                    SmartSwipeStateFlag.SUCCESS
                } else {
                    SmartSwipeStateFlag.IDLE
                }
            }
        },
        state = refreshState,
        isNeedRefresh = true,
        isNeedLoadMore = false,
        headerIndicator = {
            CustomRefreshHeader(flag = refreshState.refreshFlag, isNeedTimestamp = true)
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
            GridDailyWeatherHeaderPage(location = location, state = pageData)
            //上面是大概的信息
            //下面是其他数据,每行都是双列
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
                modifier = Modifier.padding(8.dp),
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
                    GridDailyWeatherInfo(stateHolder = weatherPagerStateHolder)
                }

                1 -> {
                    GridDayWeather(
                        stateHolder = weatherPagerStateHolder,
                        type = DayWeatherType.sevenDayWeather
                    )
                }
            }
        }

    }
}
