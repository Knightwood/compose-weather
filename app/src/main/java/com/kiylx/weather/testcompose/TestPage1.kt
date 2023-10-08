package com.kiylx.weather.testcompose

import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.kiylx.weather.common.Route
import com.kiylx.weather.common.navigateExt2
import com.kiylx.weather.common.setSavedStateResult

class TestPage1 {
    companion object {
        const val TAG = "TestPage1"
    }
}

@Composable
fun FirstPage(navController: NavController) {
    Surface {
        Column {
            Button(onClick = {
                navController.navigateExt2(Route.SETTINGS, block = {
                    this["arg1"] = "vvv"
                })
            }) {
                Text(text = "前往SecondPage")
            }
        }
    }
}

@Composable
fun SecondPage(navController: NavController) {
    Surface {
        Column {
            Button(onClick = {
                navController.setSavedStateResult("code1",Bundle().apply {
                    this.putString("data","www")
                })
                navController.popBackStack()
            }) {
                Text(text = "返回")
            }
        }
    }

}



