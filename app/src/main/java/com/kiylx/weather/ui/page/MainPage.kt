package com.kiylx.weather.ui.page

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddLocation
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kiylx.libx.http.kotlin.basic2.Resource2
import com.kiylx.weather.repo.QWeatherGeoRepo
import com.kiylx.weather.repo.QWeatherRepo
import com.kiylx.weather.repo.bean.DailyEntity
import com.kiylx.weather.repo.bean.Location
import com.kiylx.weather.ui.activitys.MainViewModel
import com.loren.component.view.composesmartrefresh.MyRefreshHeader
import com.loren.component.view.composesmartrefresh.SmartSwipeRefresh
import com.loren.component.view.composesmartrefresh.rememberSmartSwipeRefreshState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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
        PagerFragment(allLocations.value)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerFragment(locations: MutableList<Location>) {
    val pagerState = rememberPagerState() {
        locations.size
    }

    HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) {
        DailyPage(location = QWeatherGeoRepo.allLocations[it],it)
    }
}

/**
 * @param index 0即默认位置，如果开启了gps更新，就得定位刷新
 */
@Composable
fun DailyPage(location: Location, index: Int) {
    val vm: DailyViewModel = viewModel()
    val data by vm.daily.collectAsState()
    LaunchedEffect(key1 = Unit, block = {
        vm.getDailyReport(location)
    })
    //下拉刷新
    val refreshState = rememberSmartSwipeRefreshState()
    SmartSwipeRefresh(
        modifier = Modifier.fillMaxSize(),
        onRefresh = {
            vm.getDailyReport(location)
        },
        state = refreshState,
        isNeedRefresh = true,
        isNeedLoadMore = false,
        headerIndicator = {
            MyRefreshHeader(refreshState.refreshFlag, true)
        },
    ) {

    }
}

class DailyViewModel : ViewModel() {
    private val _daily: MutableStateFlow<Resource2<DailyEntity>> =
        MutableStateFlow(Resource2.EmptyLoading)
    val daily: StateFlow<Resource2<DailyEntity>>
        get() = _daily

    /**
     * 查询实时天气
     */
    suspend fun getDailyReport(
        location: Location,
    ) {
        val response = QWeatherRepo.getDailyReport(location)
        _daily.emit(response)
    }
}