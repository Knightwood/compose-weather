package com.kiylx.weather.ui.page

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.kiylx.libx.http.kotlin.basic3.UiState
import com.kiylx.weather.R
import kotlinx.coroutines.currentCoroutineContext

class ToastMsg {
    companion object {
        const val ServiceErrMsg = "获取失败，请稍后再试"
    }
}

@Composable
fun UiStateToastMsg(state: State<UiState>) {
    when (state.value) {
        UiState.Empty -> {
            ToastMsg(R.string.empty_msg)
        }

        UiState.INIT -> {}
        UiState.Loading -> {
            ToastMsg(R.string.loading_msg)
        }

        is UiState.OtherErr -> {
            val msg = (state.value as UiState.OtherErr).msg
                ?: stringResource(id = R.string.service_err_msg)
            val code = (state.value as UiState.OtherErr).code ?: -1
            ToastMsg("code:$code :$msg")
        }

        is UiState.RequestErr -> {
            ToastMsg(R.string.service_err_msg)

        }

        is UiState.Success<*> -> {
            ToastMsg(R.string.success_msg)
        }
    }
}

@Composable
fun ToastMsg(resId: Int, duration: Int = Toast.LENGTH_LONG) {
    val ctx = LocalContext.current
    Toast.makeText(ctx, resId, duration).show()
}

@Composable
fun ToastMsg(msg: String, duration: Int = Toast.LENGTH_LONG) {
    val ctx = LocalContext.current
    Toast.makeText(ctx, msg, duration).show()
}