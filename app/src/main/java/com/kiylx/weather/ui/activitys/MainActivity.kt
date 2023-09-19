package com.kiylx.weather.ui.activitys

import android.Manifest
import android.location.Location
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
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
import com.kiylx.weather.ui.page.LocationManagerPage
import com.kiylx.weather.ui.page.SettingPage
import com.kiylx.weather.ui.page.main.MainPage
import com.kiylx.weather.ui.page.splash.AddLocationPage
import com.kiylx.weather.ui.page.splash.KeySplash
import com.kiylx.weather.ui.page.splash.MainSplashPage

class MainActivity : AppCompatActivity() {
    lateinit var mainViewModel: MainViewModel

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        setContent {
            DynamicTheme {
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .fillMaxSize()
                        .systemBarsPadding()
                ) {
                    //构建导航
                    NavHost(navController = navController, startDestination = Route.HOME) {
                        animatedComposable(route = Route.HOME) {
                            val first = remember {
                                AllPrefs.firstEnter
                            }
                            if (first) {
                                navController.navigate(Route.SPLASH)
                            }
                            MainPage(
                                navigateToSettings = {
                                    navController.navigate(Route.THEME)
                                },
                                navigateToLocations = { navController.navigate(Route.LOCATION) },
                                viewModel = mainViewModel
                            )
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
    }

    private fun NavGraphBuilder.buildLocationManagerPage(
        navController: NavHostController
    ) {
        navigation(startDestination = Route.LOCATION_PAGE, route = Route.LOCATION) {
            animatedComposable(route = Route.LOCATION_PAGE) {
                LocationManagerPage(navController = navController)
            }
            animatedComposable(Route.LOCATION_ADD_PAGE) {
                AddLocationPage(::queryGps, ::stopGps) {
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
                AddLocationPage(::queryGps, ::stopGps) {
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

    //==========================gps=========================
    private var gpsHolder: GpsHolder? = null

    private fun stopGps() {
        gpsHolder?.unRegisterListener()
    }

    private fun queryGps() {
        this requestThese perms explainReason null goToSetting null finally { allGranted, grantedList, deniedList ->
            if (gpsHolder == null) {
                gpsHolder = GpsHolder.Instance.configGps(application) {
                    myLocationListener = object : MyLocationListener {
                        override fun locationChanged(
                            holder: GpsHolder.DataHolder,
                            location: Location
                        ) {
                            val str =
                                String.format("%.2f", location.longitude) + "," + String.format(
                                    "%.2f",
                                    location.latitude
                                )
                            QWeatherGeoRepo.gpsDataFlow.tryEmit(str)
                        }
                    }
                }
            }
            if (!GpsHolder.Instance.running) {
                gpsHolder?.registerListener()
            }
        }
    }

    val perms = listOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )

}