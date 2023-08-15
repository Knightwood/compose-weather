package com.kiylx.weather.ui.page.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.kiylx.weather.common.NextButton
import com.kiylx.weather.common.Route
import com.kiylx.weather.icon.R
import com.kiylx.weather.common.AllPrefs
import com.kiylx.weather.ui.activitys.MainViewModel

/**
 * 填写QWeather私有key的页面
 */
@Composable
fun KeySplash(navController: NavHostController, mainViewModel: MainViewModel) {
    val key = AllPrefs.apiKey
    //lottie动画
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(
            R.raw.woman_reading_book_under_the_tree
        )
    )
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
    )
    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        LottieAnimation(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .height(300.dp)
                .fillMaxWidth(),
            composition = composition, progress = progress
        )
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(8.dp)
        ) {
            OutlinedTextField(value = key, onValueChange = {
                AllPrefs.apiKey = it
            }, label = { Text("key") })
            Text(
                modifier = Modifier
                    .padding(16.dp),
                text = "此软件的天气服务使用和风天气驱动，请前往和风天气官网注册，并获取key值。"
            )
        }
        NextButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    bottom = 32.dp,
                    end = 16.dp
                ),
            onClick = {
                navController.navigate(Route.SPLASH_LOCATION__PAGE)
            }
        )
    }
}