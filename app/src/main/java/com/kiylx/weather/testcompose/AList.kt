package com.kiylx.weather.testcompose

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime

/**
 * 这是一个可滑动的列表
 */
@Composable
fun AList() {
    val itemsList = (0..30).toList()
    // Remember our own LazyListState
    val listState = rememberLazyListState()
        LazyColumn(
            modifier =Modifier.fillMaxSize(),
            state = listState,
        ) {
            items(itemsList) {
                    Text(
                        text = it.toString(),
                        modifier = Modifier.padding(8.dp)
                    )
            }
        }

}