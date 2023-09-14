package com.kiylx.weather.icon

import android.annotation.SuppressLint
import android.app.Application
import android.util.SparseArray
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.painter.Painter
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
            if (tmp == 0) {
                tmp = unKnowId
            }
            sparseArray[code] = tmp
            tmp
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconText(
    icon: Painter,
    iconSize: Dp = 48.dp,
    padding: Dp = 4.dp,
    title: String,//icon side title
    text: String,//icon side text
    onClick: () -> Unit = {},
    description: String? = null,//icon description
) {
    Card(
        onClick = onClick, modifier = Modifier
            .padding(padding)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(8.dp),
        ) {
            Icon(
                painter = icon,
                contentDescription = description,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.secondaryContainer,
                        CircleShape
                    )
                    .padding(8.dp)
                    .size(iconSize)
                    .align(Alignment.CenterVertically),
            )
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(start = 8.dp, end = 8.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 4.dp),
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
fun WeatherIcon(code: Int, iconSize: Dp = 48.dp, onClickListener: () -> Unit = {}) {
    val resId = WeatherIcon.getResId(code)
    Icon(
        painter = painterResource(id = resId),
        contentDescription = null,
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.secondaryContainer,
                CircleShape
            )
            .clickable {
                onClickListener()
            }
            .padding(8.dp)
            .size(iconSize),
        tint = MaterialTheme.colorScheme.onSecondaryContainer,
    )
}

@Composable
fun WeatherIconNoRound(
    code: Int,
    otherModifier: Modifier = Modifier,
    iconSize: Dp = 48.dp,
    onClickListener: () -> Unit = {}
) {
    val resId = WeatherIcon.getResId(code)
    Icon(
        painter = painterResource(id = resId),
        contentDescription = null,
        modifier = Modifier
            .clickable {
                onClickListener()
            }
            .padding(8.dp)
            .size(iconSize)
            .then(otherModifier),
        tint = MaterialTheme.colorScheme.onSecondaryContainer,
    )
}


