package com.kiylx.weather.ui.page.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kiylx.compose_lib.component.ConfirmButton
import com.kiylx.weather.R
import com.kiylx.weather.common.AllPrefs

@Composable
fun KeyEditDialog(onDismissRequest: () -> Unit = {}) {
//    val context = LocalContext.current
//    val clipboardManager = LocalClipboardManager.current
//    val scope = rememberCoroutineScope()
    var templateText by remember { mutableStateOf(AllPrefs.apiKey) }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { onDismissRequest()},
        confirmButton = {
            ConfirmButton {
                if (templateText.isBlank() || templateText.isEmpty()) {
                    isError = true
                } else {
                    AllPrefs.apiKey = templateText
                    onDismissRequest()
                }
            }
        },
        modifier = Modifier,
        icon = { Icon(imageVector = Icons.Filled.Terminal, contentDescription = null) },
        title = {
            Text(text = stringResource(R.string.edit_key))
        },
        text = {
//            val focusManager = LocalFocusManager.current
//            val softwareKeyboardController = LocalSoftwareKeyboardController.current
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = stringResource(R.string.get_key_info),
                    style = MaterialTheme.typography.bodyLarge
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    value = templateText,
                    onValueChange = {
                        templateText = it
                        isError = false
                    },
                    label = { Text(stringResource(R.string.key_label)) },
                    maxLines = 3,
                    isError = isError,
                    keyboardOptions = KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Done)
                )
            }
        }

    )
}