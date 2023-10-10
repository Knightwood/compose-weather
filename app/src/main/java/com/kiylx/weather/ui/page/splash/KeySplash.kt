package com.kiylx.weather.ui.page.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.kiylx.weather.common.AllPrefs
import com.kiylx.compose_lib.component.FloatIconTextButton
import com.kiylx.weather.common.Route
import com.kiylx.weather.icon.R
import com.kiylx.weather.ui.page.ToastMsg

/**
 * 填写QWeather私有key的页面
 */
@Composable
fun KeySplash(navController: NavHostController) {
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
    var error by remember {
        mutableStateOf(false)
    }
    if (error){
        ToastMsg(msg = stringResource(com.kiylx.weather.R.string.please_add_key))
        error=false
    }
    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize().systemBarsPadding()
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
        FloatIconTextButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    bottom = 32.dp,
                    end = 16.dp
                )
        ) {
            if (AllPrefs.apiKey.isEmpty() || AllPrefs.apiKey.isBlank()) {
                error=true
            } else {
                navController.navigate(Route.SPLASH_LOCATION_ADD_PAGE)
            }
        }
    }
}