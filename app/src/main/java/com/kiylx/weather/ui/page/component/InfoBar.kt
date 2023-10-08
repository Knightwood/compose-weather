package com.kiylx.weather.ui.page.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp

/**
 * 两边是图标，中间是文字
 * 使用：
 * ```
 * TwoIconTitleBar(modifier =Modifier
 *                          .background(
 *                 MaterialTheme.colorScheme.errorContainer,
 *                 RoundedCornerShape(8.dp)
 *             )
 *    )
 * ```
 */
@Composable
fun TwoIconTitleBar(
    modifier: Modifier = Modifier,
    startPainter: Painter,
    startPainterTint: Color = LocalContentColor.current,
    text: String,
    endPainter: Painter,
    endPainterTint: Color = LocalContentColor.current,
    paddingValues: PaddingValues = PaddingValues(
        horizontal = 16.dp,
        vertical = 4.dp
    ),
    click: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingValues)
            .clickable {
                click()
            }
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            painter = startPainter,
            contentDescription = null,
            tint = startPainterTint,
            modifier = Modifier
                .padding(8.dp)
                .weight(1f)
        )
        Text(
            text = text,
            modifier = Modifier
                .weight(5f),
            style = MaterialTheme.typography.titleMedium,
        )
        Icon(
            painter = endPainter,
            contentDescription = null,
            tint = endPainterTint,
            modifier = Modifier
                .padding(8.dp)
                .weight(1f),
        )
    }
}

/**
 * 文字跟着icon,并且充满宽度
 */
@Composable
fun IconTitleBar(
    painter: Painter,
    title: String,
    modifier: Modifier = Modifier,
    painterTint: Color = LocalContentColor.current,
    paddingValues: PaddingValues = PaddingValues(
        horizontal = 16.dp,
        vertical = 4.dp
    ),
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(paddingValues),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painter, contentDescription = null,
            tint=painterTint,
            modifier = Modifier
                .padding(top = 8.dp, bottom = 8.dp, end = 8.dp)
        )
        Text(
            text = title,
            modifier = Modifier.padding(start = 4.dp),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

/**
 * 标签样式的提示，与[IconTitleBar]同样是文字跟着icon，但不会充满宽度，仅包裹图标和文字
 */
@Composable
fun IconLabel(
    modifier: Modifier, painter: Painter,
    contentDescription: String?,
    text: String,
    tint: Color = LocalContentColor.current,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .background(
                MaterialTheme.colorScheme.primaryContainer,
                RoundedCornerShape(28.dp)
            )
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painter,
            modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
            contentDescription = contentDescription,
            tint = tint
        )
        Text(text = text, modifier = Modifier.padding(end = 16.dp))
    }
}