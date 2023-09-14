package com.kiylx.compose_lib.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 占位，可以在底部腾出导航栏的高度
 */
@Composable
fun NavigationBarSpacer(modifier: Modifier = Modifier) {
    Spacer(
        modifier = modifier.height(
            with(WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()) {
                if (this.value > 30f) this else 0f.dp
            }
        )
    )
}