package com.kiylx.weather.testcompose

import android.os.Bundle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.kiylx.compose_lib.component.PressIconButton
import com.kiylx.compose_lib.component.RippleAnimationState.AnimMode
import com.kiylx.compose_lib.component.autoRippleAnimation
import com.kiylx.compose_lib.component.rememberRippleAnimationState
import com.kiylx.compose_lib.theme3.DarkThemePrefs
import com.kiylx.compose_lib.theme3.LocalDarkThemePrefs
import com.kiylx.compose_lib.theme3.LocalWindows
import com.kiylx.compose_lib.theme3.ThemeHelper.modifyDarkThemePreference
import com.kiylx.weather.common.Route
import com.kiylx.weather.common.navigateExt2
import com.kiylx.weather.common.setSavedStateResult

const val TAG = "TestPage1"

@Composable
fun FirstPage(navController: NavController) {
    val scope = rememberCoroutineScope()
    val isDark = LocalDarkThemePrefs.current.isDarkTheme()
    val rippleAnimationState = rememberRippleAnimationState {
        animTime = 5000
        moveUpSystemBarInsts=true
    }
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .autoRippleAnimation(LocalWindows.current,rippleAnimationState)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Button(onClick = {
                navController.navigateExt2(Route.SETTINGS, block = {
                    this["arg1"] = "vvv"
                })
            }) {
                Text(text = "前往SecondPage")
            }

            Button(
                modifier = Modifier,
                onClick = {
                    //设置动画效果
                    if (isDark) {
                        rippleAnimationState.animMode = AnimMode.shrink
                    } else {
                        rippleAnimationState.animMode = AnimMode.expend
                    }
                    //调用此方法执行动画
                    rippleAnimationState.change(){
                        //主题切换
                        if (isDark) {
                            scope.modifyDarkThemePreference(darkThemeMode = DarkThemePrefs.OFF)
                        } else {
                            scope.modifyDarkThemePreference(darkThemeMode = DarkThemePrefs.ON)
                        }
                    }
                }) {
                Text(text = "切换主题")
            }

            PressIconButton(
                modifier = Modifier,
                onClick = {},
                icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = null) },
                text = { Text("Add to cart") }
            )
        }
    }
}

@Composable
fun SecondPage(navController: NavController) {
    Surface {
        Column {
            Button(onClick = {
                navController.setSavedStateResult("code1", Bundle().apply {
                    this.putString("data", "www")
                })
                navController.popBackStack()
            }) {
                Text(text = "返回")
            }
        }
    }

}



