package com.kiylx.compose_lib.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


object DefaultCard {
    val defaultBackground
        @Composable
        get() =
            SurfaceCardBackground(
                shape = MaterialTheme.shapes.medium,
                shadowElevation = 6.dp
            )

    val flattenBackground
        @Composable
        get() =
            SurfaceCardBackground(
                shape = MaterialTheme.shapes.medium,
                shadowElevation = 2.dp
            )
}

@Immutable
data class SurfaceCardBackground(
    val shape: Shape,
    val shadowElevation: Dp,
)

@Composable
fun SurfaceCard(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues=PaddingValues(8.dp),
    color: Color = MaterialTheme.colorScheme.surfaceContainer,
    background: SurfaceCardBackground = DefaultCard.defaultBackground,
    content: @Composable () -> Unit
) {
    Surface(
        color = color,
        shadowElevation = background.shadowElevation,
        shape = background.shape,
        modifier = modifier.padding(paddingValues),
        content = content
    )
}