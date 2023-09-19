package com.kiylx.weather.ui.page.splash

import android.os.Process
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.kiylx.weather.AppCtx
import com.kiylx.weather.common.FloatIconTextButton
import com.kiylx.weather.common.Route
import kotlin.system.exitProcess

/**
 * 引导页主页
 */
@Composable
fun MainSplashPage(navController: NavHostController) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(com.kiylx.weather.icon.R.raw.girl_cycling_autumn))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
    )
    var firstTime by remember {
        mutableStateOf(System.currentTimeMillis())
    }
    BackHandler(true) {
        val secondTime = System.currentTimeMillis()
        if (secondTime - firstTime > 2000) {
            Toast.makeText(AppCtx.instance, "再按一次退出程序", Toast.LENGTH_SHORT).show()
            firstTime = secondTime
        } else {
            Process.killProcess(Process.myPid())
            exitProcess(0)
        }
    }
    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text(
            text = "Compose Weather",
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.align(Alignment.TopCenter)
        )
        LottieAnimation(
            composition = composition, progress = progress
        )
        FloatIconTextButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    bottom = 32.dp,
                    end = 16.dp
                )
        ) {
            navController.navigate(Route.KEY_PAGE)
        }
        Text(
            text = "天气服务由和风天气驱动",
            modifier = Modifier.align(Alignment.BottomCenter),
            color = Color.Gray
        )
    }

}