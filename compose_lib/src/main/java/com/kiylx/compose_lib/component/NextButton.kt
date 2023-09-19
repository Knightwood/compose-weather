package com.kiylx.compose_lib.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowCircleRight
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.kiylx.compose_lib.R

@Composable
fun FloatIconTextButton(modifier: Modifier, text:String= stringResource(R.string.next_step), onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        modifier = modifier,
        text = { Text(text = text) },
        icon = {
            Icon(
                imageVector = Icons.Rounded.ArrowCircleRight,
                contentDescription = text
            )
        },
        onClick = onClick
    )
}

@Composable
fun CompleteButton(modifier: Modifier, onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        modifier = modifier,
        text = { Text(text = "完成") },
        icon = {
            Icon(
                imageVector = Icons.Rounded.Done,
                contentDescription = "完成"
            )
        },
        onClick = onClick
    )
}

/**
 * 定位
 */
@Composable
fun LocationButton(modifier: Modifier, onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        modifier = modifier,
        text = { Text(text = "定位") },
        icon = {
            Icon(
                imageVector = Icons.Rounded.LocationOn,
                contentDescription = "定位"
            )
        },
        onClick = onClick
    )
}