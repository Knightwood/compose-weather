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
import androidx.compose.material3.Surface
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
import com.kiylx.weather.R
import com.kiylx.weather.ui.page.main.DayWeatherType

@Composable
fun GridWeatherPage(weatherPagerStateHolder: GridWeatherPagerStateHolder) {
    //位置信息
    val location = weatherPagerStateHolder.location.value
    //当天的天气状况
    val data = weatherPagerStateHolder.dailyUiState

    val pageData = data.collectAsState()//页面数据

    LaunchedEffect(key1 = Unit, block = {
        weatherPagerStateHolder.getDailyData()
    })
    Surface(modifier=Modifier.systemBarsPadding()) {
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
