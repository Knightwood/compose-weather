package com.kiylx.weather.ui.page.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp

@Composable
fun TitleCard(
    icon: ImageVector,
    title: String,
    paddingValues: PaddingValues = PaddingValues(
        horizontal = 16.dp,
        vertical = 6.dp
    ),
    shape: Shape = CardDefaults.shape,
    colors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(),
    border: BorderStroke? = null,
    content: @Composable() (ColumnScope.() -> Unit)
) {
    TitleCard(
        painter = rememberVectorPainter(image = icon),
        title = title,
        paddingValues = paddingValues,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        content = content,
    )
}

@Composable
fun TitleCard(
    painter: Painter,
    title: String,
    paddingValues: PaddingValues = PaddingValues(
        horizontal = 16.dp,
        vertical = 6.dp
    ),
    shape: Shape = CardDefaults.shape,
    colors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(),
    border: BorderStroke? = null,
    content: @Composable() (ColumnScope.() -> Unit)
) {
    Card(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxWidth(),
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
    ) {
        //标题
        IconTitleBar(painter = painter, title = title)
        content()
    }
}