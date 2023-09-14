package com.kiylx.compose_lib.pages.appearance


import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Contrast
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import com.kiylx.compose_lib.R
import com.kiylx.compose_lib.component.BackButton
import com.kiylx.compose_lib.component.LargeTopAppBar
import com.kiylx.compose_lib.component.PreferenceSingleChoiceItem
import com.kiylx.compose_lib.component.PreferenceSubtitle
import com.kiylx.compose_lib.component.PreferenceSwitch
import com.kiylx.compose_lib.theme3.DarkThemePrefs.Companion.FOLLOW_SYSTEM
import com.kiylx.compose_lib.theme3.DarkThemePrefs.Companion.OFF
import com.kiylx.compose_lib.theme3.DarkThemePrefs.Companion.ON
import com.kiylx.compose_lib.theme3.LocalDarkThemePrefs
import com.kiylx.compose_lib.theme3.ThemeHelper
import com.kiylx.compose_lib.theme3.ThemeHelper.modifyDarkThemePreference


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DarkThemePreferences(onBackPressed: () -> Unit) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true }
    )
    val scope = rememberCoroutineScope()
    val darkThemePreference = LocalDarkThemePrefs.current
    val isHighContrastModeEnabled = darkThemePreference.isHighContrastModeEnabled
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        modifier = Modifier,
                        text = stringResource(id = R.string.dark_theme),
                    )
                }, navigationIcon = {
                    BackButton {
                        onBackPressed()
                    }
                }, scrollBehavior = scrollBehavior
            )
        }, content = {
            LazyColumn(modifier = Modifier.padding(it)) {
                if (Build.VERSION.SDK_INT >= 29)
                    item {
                        PreferenceSingleChoiceItem(
                            text = stringResource(R.string.follow_system),
                            selected = darkThemePreference.darkThemeMode == FOLLOW_SYSTEM
                        ) {
                            scope.modifyDarkThemePreference(FOLLOW_SYSTEM)
                        }
                    }
                item {
                    PreferenceSingleChoiceItem(
                        text = stringResource(R.string.on),
                        selected = darkThemePreference.darkThemeMode == ON
                    ) { scope.modifyDarkThemePreference(ON) }
                }
                item {
                    PreferenceSingleChoiceItem(
                        text = stringResource(R.string.off),
                        selected = darkThemePreference.darkThemeMode == OFF
                    ) { scope.modifyDarkThemePreference(OFF) }
                }
                item {
                    PreferenceSubtitle(text = stringResource(R.string.additional_settings))
                }
                item {
                    PreferenceSwitch(
                        title = stringResource(R.string.high_contrast),
                        icon = Icons.Outlined.Contrast,
                        isChecked = isHighContrastModeEnabled, onClick = {
                            scope.modifyDarkThemePreference(isHighContrastModeEnabled = !isHighContrastModeEnabled)
                        }
                    )
                }
            }
        })
}