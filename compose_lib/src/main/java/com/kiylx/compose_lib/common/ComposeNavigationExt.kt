package com.kiylx.compose_lib.common

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator

/**
 * 在compose中，使用bundle传参
 * 用例：
 * ```
 * //导航到Route.SETTINGS时传递参数
 * navController.navigateExt(Route.SETTINGS, Bundle())
 *
 * //构建好的Route.SETTINGS导航
 * composable(Route.SETTINGS) {
 *     //参数获取
 *     it.arguments
 * }
 * ```
 */
fun NavController.navigateExt(
    route: String,
    args: Bundle? = null,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null,
) {
    val dest = this.graph.findNode(route)!!
    this.navigate(dest.id, args, navOptions, navigatorExtras)
}

/**
 * 在compose中，使用bundle传参的另一种方式，需要使用[getPrevSavedState]配合
 *```
 * //导航的时候传递参数
 * navController.navigateExt2(Route.SETTINGS, block = {
 *       this["arg1"] = "vvv"//参数的key-value
 * })
 * //获取参数
 * var n: String? = ""
 * navController.getPrevSavedState {
 *    n = get<String>("arg1")//取出参数
 * }
 *```
 */
fun NavController.navigateExt2(
    route: String,
    block: SavedStateHandle.() -> Unit,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null,
) {
    this.currentBackStackEntry?.savedStateHandle?.let {
        it.block()
    }
    this.navigate(route, navOptions, navigatorExtras)
}

fun NavController.getPrevSavedState(block: SavedStateHandle.() -> Unit) {
    this.previousBackStackEntry?.savedStateHandle?.let {
        it.block()
    }
}

/**
 * 使用时，A->B,B然后返回。A->B时要把自己定义的targetLivedataTag字符串作为导航的参数传给B。
 * ```
 * composable(route = Route.HOME) { it: NavBackStackEntry ->
 * //观察回传参数
 *     navController.observeSavedStateResult("code1") { re ->
 *         re.observe(it) { bundle ->
 *             Log.d(
 *                 "tty1-NavigatorParams",
 *                 "得到回传参数：${bundle.getString("data")}"
 *             )
 *             re.removeObservers(it) //使用完传回数据后，取消观察
 *         }
 *     }
 *     FirstPage(navController)
 * }
 * ```
 */
fun NavController.observeSavedStateResult(targetLivedataTag: String,block: (data: LiveData<Bundle>) -> Unit) {
    this.currentBackStackEntry?.let { backStackEntry ->
        val data =
            backStackEntry.savedStateHandle.getLiveData<Bundle>(targetLivedataTag)
        block(data)
    } ?:Log.e("tty1-observeSavedStateResult","currentBackStackEntry is null")
}

/**
 * 设置回传参数并返回，不要在不返回的情况下直接设置回传参数，可能会有问题
 * ```
 * Button(onClick = {
 *     navController.setSavedStateResult("code1",Bundle().apply {
 *         this.putString("data","www")
 *     })
 *     navController.popBackStack()
 * }) {
 *     Text(text = "返回")
 * }
 *```
 */
fun NavController.setSavedStateResult(
    targetLivedataTag: String,
    value: Bundle,
) {
    this.previousBackStackEntry?.savedStateHandle?.getLiveData<Bundle>(targetLivedataTag)?.postValue(value)
        ?:Log.e("tty1-setSavedStateResult","previousBackStackEntry is null")
}


