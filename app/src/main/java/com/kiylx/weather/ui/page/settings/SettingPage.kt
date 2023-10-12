package com.kiylx.weather.ui.page.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.DisplaySettings
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.GpsNotFixed
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
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
import com.kiylx.compose_lib.component.PreferenceSwitch
import com.kiylx.compose_lib.component.SettingItem
import com.kiylx.weather.R
import com.kiylx.weather.common.AUnit
import com.kiylx.weather.common.AllPrefs
import com.kiylx.weather.common.Route
import com.kiylx.weather.common.WindUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingPage(navController: NavHostController) {
    var gpsAutoState by remember {
        mutableStateOf(AllPrefs.gpsAuto)
    }
    var gridWeatherState by remember {
        mutableStateOf(AllPrefs.gridWeather)
    }
    var unitState by remember {
        mutableStateOf(AllPrefs.unit == AUnit.MetricUnits.param)
    }
    var speedUnitState by remember {
        mutableStateOf(AllPrefs.windUnit == WindUnit.Km)
    }
    var langState by remember {
        mutableStateOf(AllPrefs.lang)
    }
    var openDialog by remember { mutableStateOf(false) }

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
        if (openDialog) {
            KeyEditDialog {
                openDialog = false
            }
        }
        LazyColumn(modifier = Modifier.padding(it)) {
            item(key = R.string.edit_key) {
                //key
                PreferenceItem(
                    icon = Icons.Filled.Terminal,
                    title = stringResource(id = R.string.edit_key),
                    description = stringResource(R.string.update_your_api_key)
                ) {
                    openDialog = true
                }
            }
            item(key = R.string.gps_auto) {
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
            item(key = R.string.grid_weather) {
                AnimatedVisibility(
                    visible = gpsAutoState,
                    enter = slideInHorizontally(),
                    exit = slideOutHorizontally()+ shrinkOut(),
                ) {
                    //gps
                    PreferenceSwitch(
                        title = stringResource(R.string.grid_weather),
                        description = stringResource(R.string.gps_auto_get_weather),
                        icon = if (gridWeatherState) Icons.Filled.GpsFixed else Icons.Filled.GpsNotFixed,
                        isChecked = gridWeatherState
                    ) {
                        gridWeatherState = it
                        AllPrefs.gridWeather = it
                    }
                }

            }
            item(key = R.string.temp_unit) {
                PreferenceSwitch(
                    title = stringResource(R.string.temp_unit),
                    description = stringResource(R.string.use_metric_unit),
                    icon = Icons.Filled.Public,
                    isChecked = unitState
                ) {
                    unitState = it
                    AllPrefs.unit = if (it) AUnit.MetricUnits.param else
                        AUnit.ImperialUnits.param
                }
            }
            item(key = R.string.wind_speed_unit) {
                PreferenceSwitch(
                    title = stringResource(id = R.string.wind_speed_unit),
                    description = stringResource(R.string.wind_speed_use_km_unit),
                    icon = Icons.Filled.AcUnit,
                    isChecked = speedUnitState
                ) {
                    speedUnitState = it
                    AllPrefs.windUnit = if (it) WindUnit.Km else
                        WindUnit.BeaufortScale
                }
            }
//            item {
//                PreferenceItem(
//                    title = "缓存设置", description = stringResource(
//                        id = R.string.cache_settings
//                    ), icon = Icons.Filled.Cached
//                ) {
//                    navController.navigate(Route.CACHE_PAGE) {
//                        launchSingleTop = true
//                    }
//                }
//            }
            item(key = R.string.display) {
                SettingItem(
                    title = stringResource(R.string.display), description = stringResource(
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
}