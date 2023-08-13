package com.kiylx.weather.icon

import android.annotation.SuppressLint
import android.app.Application
import android.util.SparseArray
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object WeatherIcon {
    const val unKnowId = 999

    //save all icon
    private val sparseArray: SparseArray<Int> = SparseArray()
    private lateinit var ctx: Application

    fun init(ctx: Application) {
        this.ctx = ctx
    }

    /**
     * get mipmap ResId by code
     */
    @SuppressLint("DiscouragedApi")
    fun getResId(code: Int = unKnowId): Int {
        return sparseArray[code] ?: let {
            var tmp: Int = ctx.resources.getIdentifier(
                "u_$code",
                "mipmap",
                ctx.packageName
            )
            if (tmp==0){
                tmp= unKnowId
            }
            sparseArray[code] = tmp
            tmp
        }
    }

}

@Composable
fun WeatherIcon(code: Int, size: Dp = 36.dp, onClickListener: () -> Unit = {}) {
    val resId = WeatherIcon.getResId(code)
    Surface(
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.background,
                CircleShape
            )
            .clickable { onClickListener() }
            .padding(8.dp)
    ) {
        Icon(
            painter = painterResource(id = resId),
            contentDescription = null,
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.background
                )
                .size(size),
            tint = MaterialTheme.colorScheme.secondary,
        )
    }
}
