package com.kiylx.weather.ui.activitys

import android.Manifest
import android.location.Location
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.kiylx.compose_lib.pages.appearance.AppearancePreferences
import com.kiylx.compose_lib.pages.appearance.DarkThemePreferences
import com.kiylx.compose_lib.theme3.DynamicTheme
import com.kiylx.libx.tools.explainReason
import com.kiylx.libx.tools.finally
import com.kiylx.libx.tools.goToSetting
import com.kiylx.libx.tools.requestThese
import com.kiylx.libx.tools.sonser.gps.GpsHolder
import com.kiylx.libx.tools.sonser.gps.MyLocationListener
import com.kiylx.weather.common.AllPrefs
import com.kiylx.weather.common.Route
import com.kiylx.compose_lib.common.animatedComposable
import com.kiylx.weather.repo.QWeatherGeoRepo
import com.kiylx.weather.repo.bean.LocationEntity
import com.kiylx.weather.ui.page.LocationManagerPage
import com.kiylx.weather.ui.page.SettingPage
import com.kiylx.weather.ui.page.main.MainPage
import com.kiylx.weather.ui.page.splash.AddLocationPage
import com.kiylx.weather.ui.page.splash.KeySplash
import com.kiylx.weather.ui.page.splash.MainSplashPage

class MainActivity : AppCompatActivity() {
    lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        setContent {
            DynamicTheme {
                val navController = rememberNavController()
                //构建导航
                NavHost(navController = navController, startDestination = Route.HOME) {
                    animatedComposable(route = Route.HOME) {
                        val first = remember {
                            AllPrefs.firstEnter
                        }
                        //如果开启gps实时定位获取网格天气
                        if (AllPrefs.gpsAuto){
                            queryGps()
                        }
                        if (first) {
                            navController.navigate(Route.SPLASH)
                        } else {
                            MainPage(
                                navigateToSettings = {
                                    navController.navigate(Route.THEME)
                                },
                                navigateToLocations = { navController.navigate(Route.LOCATION) },
                                viewModel = mainViewModel
                            )
                        }
                    }

                    //位置添加页面
                    buildLocationManagerPage(navController)
                    //构建设置页面的嵌套导航
                    buildSettingsPage(navController)
                    buildSplashPage(navController)
                }

            }
        }
    }

    private fun NavGraphBuilder.buildLocationManagerPage(
        navController: NavHostController
    ) {
        navigation(startDestination = Route.LOCATION_PAGE, route = Route.LOCATION) {
            animatedComposable(route = Route.LOCATION_PAGE) {
                LocationManagerPage(navController = navController)
            }
            animatedComposable(Route.LOCATION_ADD_PAGE) {
                AddLocationPage(::queryGps, ::stopGps, mainViewModel) {
                    //位置添加完成
                    navController.navigate(Route.LOCATION_PAGE, navOptions {
                        this.popUpTo(Route.LOCATION_PAGE)
                        this.launchSingleTop = true
                    })
                    QWeatherGeoRepo.addLocation(it, false)
                }
            }
        }
    }

    private fun NavGraphBuilder.buildSplashPage(
        navController: NavHostController
    ) {
        navigation(startDestination = Route.SPLASH_PAGE, route = Route.SPLASH) {
            animatedComposable(Route.SPLASH_PAGE) {
                MainSplashPage(navController)
            }
            animatedComposable(Route.KEY_PAGE) {
                KeySplash(navController)
            }
            animatedComposable(Route.SPLASH_LOCATION_ADD_PAGE) {
                AddLocationPage(::queryGps, ::stopGps, mainViewModel) {
                    //完成定位设置，前往主页
                    //并设置默认位置
                    AllPrefs.firstEnter = false
                    navController.navigate(Route.HOME, navOptions {
                        this.popUpTo(Route.HOME)
                        this.launchSingleTop = true
                    })
                    QWeatherGeoRepo.addLocation(it, true)
                }
            }
        }
    }

    /**
     * 主页面导航到设置页面，设置页面有很多的其他页面导航，
     * 所以将设置页面的导航在这里设置。即嵌套导航。
     */
    private fun NavGraphBuilder.buildSettingsPage(
        navController: NavHostController,
        onBackPressed: () -> Unit = { navController.popBackStack() }
    ) {
        navigation(startDestination = Route.SETTINGS_PAGE, route = Route.SETTINGS) {
            animatedComposable(Route.SETTINGS_PAGE) {
                SettingPage(navController)
            }
            animatedComposable(Route.THEME) {
                AppearancePreferences(navController = navController) {
                    navController.navigate(Route.DARK_THEME)
                }
            }
            animatedComposable(Route.DARK_THEME) {
                DarkThemePreferences {
                    navController.popBackStack()
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        stopGps(true)
    }

    //==========================gps=========================
    private var gpsHolder: GpsHolder? = null

    /**
     * 如果传true，则不论有无开启gps获取天气，都会关闭gps
     */
    private fun stopGps(realStop: Boolean = false) {
        if (!AllPrefs.gpsAuto || realStop) {
            gpsHolder?.unRegisterListener()
        }
    }

    private fun queryGps() {
        this requestThese gpsPerms explainReason null goToSetting null finally { allGranted, grantedList, deniedList ->
            if (gpsHolder == null) {
                gpsHolder = GpsHolder.Instance.configGps(application) {
                    this.requestUpdateDistanceInterval = 0F
                    myLocationListener = object : MyLocationListener {
                        override fun locationChanged(
                            holder: GpsHolder.DataHolder,
                            location: Location
                        ) {
                            //通过gps添加位置信息或获取网格天气都是用的同一个方法，
                            //因此，通过判断mainViewModel.addLocationActionState 判断，做到添加位置信息时不会错误填写
                            if (mainViewModel.addLocationActionState.value) {
                                val lon = String.format("%.2f", location.longitude)
                                val lat = String.format("%.2f", location.latitude)
                                val str = "${lon},${lat}"
                                //经纬度字符串
                                mainViewModel.addLocationStateHolder.gpsStr.tryEmit(str)
                            }
                            if (AllPrefs.gpsAuto) {
                                //经纬度实体
                                val locationEntity = LocationEntity(
                                    lat = location.latitude.toString(),
                                    lon = location.longitude.toString()
                                )
                                QWeatherGeoRepo.gpsDataState.value = locationEntity
                            }

                        }
                    }
                }
            }
            if (!GpsHolder.Instance.running) {
                gpsHolder?.registerListener()
            }
        }
    }

    val gpsPerms = listOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )

}