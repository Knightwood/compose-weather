package com.kiylx.weather.ui.activitys

import android.Manifest
import android.location.Location
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kiylx.libx.tools.explainReason
import com.kiylx.libx.tools.finally
import com.kiylx.libx.tools.goToSetting
import com.kiylx.libx.tools.requestThese
import com.kiylx.libx.tools.sonser.gps.GpsHolder
import com.kiylx.libx.tools.sonser.gps.MyLocationListener
import com.kiylx.weather.common.Route
import com.kiylx.weather.common.animatedComposable
import com.kiylx.weather.common.AllPrefs
import com.kiylx.weather.repo.QWeatherGeoRepo
import com.kiylx.weather.ui.page.LocationPage
import com.kiylx.weather.ui.page.main.MainPage
import com.kiylx.weather.ui.page.SettingPage
import com.kiylx.weather.ui.page.splash.AddLocationPage
import com.kiylx.weather.ui.page.splash.KeySplash
import com.kiylx.weather.ui.page.splash.MainSplashPage
import com.kiylx.weather.ui.theme.WeatherTheme

class MainActivity : AppCompatActivity() {
    lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        setContent {
            WeatherTheme {
                val navController = rememberNavController()
                val systemUiController = rememberSystemUiController()
                val useDarkIcons = !isSystemInDarkTheme()
                SideEffect() {
                    systemUiController.setSystemBarsColor(
                        color = Color.Transparent,
                        darkIcons = useDarkIcons,
                        isNavigationBarContrastEnforced = false,
                    )
                }
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
                            } else {
                                MainPage(
                                    navigateToSettings = { navController.navigate(Route.SETTINGS) },
                                    navigateToLocations = { navController.navigate(Route.LOCATION_PAGE) },
                                    viewModel = mainViewModel
                                )
                            }
                        }
                        animatedComposable(route = Route.LOCATION_PAGE) {
                            LocationPage()
                        }
                        //构建设置页面的嵌套导航
                        buildSettingsPage(navController, mainViewModel)
                        buildSplashPage(navController, mainViewModel)
                    }
                }
            }
        }
    }

    private fun NavGraphBuilder.buildSplashPage(
        navController: NavHostController,
        mainViewModel: MainViewModel
    ) {
        navigation(startDestination = Route.SPLASH_PAGE, route = Route.SPLASH) {
            animatedComposable(Route.SPLASH_PAGE) {
                MainSplashPage(navController, mainViewModel)
            }
            animatedComposable(Route.KEY_PAGE) {
                KeySplash(navController, mainViewModel)
            }
            animatedComposable(Route.SPLASH_LOCATION__PAGE) {
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
        mainViewModel: MainViewModel,
        onBackPressed: () -> Unit = { navController.popBackStack() }
    ) {
        navigation(startDestination = Route.SETTINGS_PAGE, route = Route.SETTINGS) {
            animatedComposable(Route.SETTINGS_PAGE) {
                SettingPage(navController)
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