package com.kiylx.weather.ui.page.component

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kiylx.weather.R

@Composable
fun TitleCard(icon: ImageVector, title: String, content: @Composable() (ColumnScope.() -> Unit)) {
    TitleCard(painter = rememberVectorPainter(image = icon), title = title, content = content)
}

@Composable
fun TitleCard(painter: Painter, title: String, content: @Composable() (ColumnScope.() -> Unit)) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
    ) {
        //标题
        Row(
            modifier = Modifier
                .padding(start = 16.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painter = painter, contentDescription = null)
            Text(
                text = title,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        content()
    }
}