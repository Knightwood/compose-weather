package com.kiylx.weather.ui.page.component

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp

@Composable
fun TitleCard(icon: ImageVector, title: String, content: @Composable() (ColumnScope.() -> Unit)) {
    TitleCard(painter = rememberVectorPainter(image = icon), title = title, content = content)
}

@Composable
fun TitleCard(
    painter: Painter,
    title: String,
    paddingValues: PaddingValues = PaddingValues(
        horizontal = 16.dp,
        vertical = 6.dp
    ),
    content: @Composable() (ColumnScope.() -> Unit)
) {
    Card(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxWidth()
    ) {
        //标题
        IconTitleBar(painter = painter, title = title)
        content()
    }
}