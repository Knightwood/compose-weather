package com.kiylx.weather.ui.page.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.DisplaySettings
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.GpsNotFixed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.kiylx.compose_lib.component.BackButton
import com.kiylx.compose_lib.component.LargeTopAppBar
import com.kiylx.compose_lib.component.PreferenceItem
import com.kiylx.compose_lib.component.PreferenceSubtitle
import com.kiylx.compose_lib.component.PreferenceSwitch
import com.kiylx.compose_lib.component.SettingItem
import com.kiylx.compose_lib.component.SettingTitle
import com.kiylx.compose_lib.component.SmallTopAppBar
import com.kiylx.weather.R
import com.kiylx.weather.common.AllPrefs
import com.kiylx.weather.common.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingPage(navController: NavHostController) {
    var gpsAutoState by remember {
        mutableStateOf(AllPrefs.gpsAuto)
    }
    var gridWeatherState by remember {
        mutableStateOf(AllPrefs.gridWeather)
    }
    // 滑动的行为
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true })
    //页面显示的开始，用脚手架打底
    Scaffold(modifier = Modifier
        .fillMaxSize()
        .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            //顶部的导航栏，TopAppBar.kt文件
            LargeTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.settings))
                },
                navigationIcon = { BackButton { navController.popBackStack() } },
                scrollBehavior = scrollBehavior
            )
        }) {
        LazyColumn(modifier = Modifier.padding(it)) {
            item {
                //gps
                PreferenceSwitch(
                    title = "Gps",
                    description = stringResource(R.string.gps_auto),
                    icon = if (gpsAutoState) Icons.Filled.GpsFixed else Icons.Filled.GpsNotFixed,
                    isChecked = gpsAutoState
                ) {
                    gpsAutoState = it
                    AllPrefs.gpsAuto = it
                }
            }
            if (gpsAutoState) {
                item {
                    //gps
                    PreferenceSwitch(
                        title = "格点天气",
                        description = stringResource(R.string.gps_auto_get_weather),
                        icon = if (gridWeatherState) Icons.Filled.GpsFixed else Icons.Filled.GpsNotFixed,
                        isChecked = gridWeatherState
                    ) {
                        gridWeatherState=it
                        AllPrefs.gridWeather = it
                    }
                }
            }
            item {
                PreferenceItem(
                    title = "缓存设置", description = stringResource(
                        id = R.string.cache_settings
                    ), icon = Icons.Filled.Cached
                ) {
                    navController.navigate(Route.CACHE_PAGE) {
                        launchSingleTop = true
                    }
                }
            }
            item {
                SettingItem(
                    title = "显示", description = stringResource(
                        id = R.string.display_settings
                    ), icon = Icons.Filled.DisplaySettings
                ) {
                    navController.navigate(Route.THEME) {
                        launchSingleTop = true
                    }
                }
            }
        }
    }


    //缓存
    //显示
    //关于
}