package com.kiylx.compose_lib.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 持有一些sheet的配置和状态，以及显示和隐藏的方法,配合[MBottomSheet]方法使用
 */
@OptIn(ExperimentalMaterial3Api::class)
class MBottomSheetHolder(
    val openBottomSheet: MutableState<Boolean> = mutableStateOf(false),
    val edgeToEdgeEnabled: MutableState<Boolean> = mutableStateOf(false),
    val skipPartiallyExpanded: MutableState<Boolean> = mutableStateOf(false),
) {
    var bottomSheetState: SheetState? = null

    // Note: If you provide logic outside of onDismissRequest to remove the sheet,
    // you must additionally handle intended state cleanup, if any.
    fun hide(coroutineScope: CoroutineScope) {
        bottomSheetState?.let {state->
            coroutineScope.launch { state.hide() }.invokeOnCompletion {
                if (!state.isVisible) {
                    openBottomSheet.value = false
                }
            }
        }
    }

    fun show() {
        openBottomSheet.value = true
    }
}

/**
 * 使用方法：
 *
 * ```
 * val scope = rememberCoroutineScope()
 * val warnBottomSheetHolder by remember {
 *     mutableStateOf(MBottomSheetHolder())
 * }
 *
 * MBottomSheet(
 *         sheetHolder = warnBottomSheetHolder,
 *         scope = scope
 *     ) {
 *        //内容
 * }
 * ```
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MBottomSheet(
    sheetHolder: MBottomSheetHolder,
    scope: CoroutineScope,
    content: @Composable ColumnScope.() -> Unit,
) {
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = sheetHolder.skipPartiallyExpanded.value//是否直接展开到全部
    )
    sheetHolder.bottomSheetState = bottomSheetState//赋值

    // Sheet content
    if (sheetHolder.openBottomSheet.value) {
        val windowInsets = if (sheetHolder.edgeToEdgeEnabled.value)
            WindowInsets(0) else BottomSheetDefaults.windowInsets
        ModalBottomSheet(
            onDismissRequest = {
                sheetHolder.hide(scope)
            },
            sheetState = bottomSheetState,
            windowInsets = windowInsets
        ) {
            content()
            NavigationBarSpacer(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun DrawerSheetSubtitle(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    Text(
        text = text,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 4.dp, top = 16.dp, bottom = 8.dp),
        color = color,
        style = MaterialTheme.typography.labelLarge
    )
}