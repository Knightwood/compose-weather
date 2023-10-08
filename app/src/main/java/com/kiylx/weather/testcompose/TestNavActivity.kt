package com.kiylx.weather.testcompose

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kiylx.compose_lib.common.animatedComposable
import com.kiylx.compose_lib.theme3.DynamicTheme
import com.kiylx.weather.common.Route
import com.kiylx.weather.common.getPrevSavedState
import com.kiylx.weather.common.navigateExt
import com.kiylx.weather.common.observeSavedStateResult
import com.kiylx.weather.ui.activitys.LocalNavController
import com.kiylx.weather.ui.activitys.MainViewModel
import com.kiylx.weather.ui.theme.WeatherTheme


class TestNavActivity : AppCompatActivity() {
    lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        setContent {
            val navController = rememberNavController()
            DynamicTheme {
                //构建导航
                NavHost(navController = navController, startDestination = Route.HOME) {
                    composable(route = Route.HOME) {
                        navController.observeSavedStateResult("code1") { re ->
                            re.observe(it) { bundle ->
                                Log.d(
                                    "tty1-NavigatorParams",
                                    "得到回传参数：${bundle.getString("data")}"
                                )
                                re.removeObservers(it) //使用完传回数据后，取消观察
                            }
                        }
                        FirstPage(navController)
                    }
                    composable(Route.SETTINGS) {
                        var count by remember {
                            mutableIntStateOf(0)
                        }
                        var n: String? = ""
                        navController.getPrevSavedState {
                            n = get<String>(
                                "arg1"
                            )
                        }
                        SideEffect {
                            count += 1
                            Log.d(TestPage1.TAG, "HomePage: $count")
                            Log.d(TestPage1.TAG, "HomePage: $n")
                        }
                        SecondPage(navController = navController)
                    }
                }
            }
        }
    }

    private fun NavGraphBuilder.testNavArgs(
        navController: NavHostController,
    ) {
        //1. 导航使用了网址这样的形式传参
        navController.navigate(Route.SETTINGS + "/{a}" + "/{b}" + "?arg3={c}")
        animatedComposable(
            Route.SETTINGS + "/{arg1}" + "/{arg2}" + "?arg3={arg3}",
            arguments = listOf(
                navArgument("arg1") {
                    this.type = NavType.StringType
                },
                navArgument("arg2") {
                    this.type = NavType.StringType
                }, navArgument("arg3") {
                    this.type = NavType.StringType
                    this.defaultValue = "222"
                }
            )
        ) {
            //参数获取
            it.arguments?.getString("arg1")

        }

        //2.  iiiiiiiiiiiiiiiiii
        //导航到Route.SETTINGS时传递参数
        navController.navigateExt(Route.SETTINGS, Bundle())

        //构建好的Route.SETTINGS导航
        composable(Route.SETTINGS) {
            //参数获取
            it.arguments
        }

        //3. iiiiiiiiiiiiiiiiii
        navController.currentBackStackEntry?.savedStateHandle?.set("arg", "value")
        navController.navigate(Route.SETTINGS)
        animatedComposable(
            Route.SETTINGS
        ) {
            //参数获取
            navController.previousBackStackEntry?.savedStateHandle?.get<String>(
                "arg"
            )
        }

    }

}