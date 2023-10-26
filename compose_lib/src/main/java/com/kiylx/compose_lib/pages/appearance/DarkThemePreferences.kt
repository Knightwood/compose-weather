package com.kiylx.compose_lib.pages.appearance


import android.os.Build
import android.view.Window
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Contrast
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kiylx.compose_lib.R
import com.kiylx.compose_lib.component.BackButton
import com.kiylx.compose_lib.component.LargeTopAppBar
import com.kiylx.compose_lib.component.PreferenceSingleChoiceItem
import com.kiylx.compose_lib.component.PreferenceSubtitle
import com.kiylx.compose_lib.component.PreferenceSubtitleNotFillWidth
import com.kiylx.compose_lib.component.PreferenceSwitch
import com.kiylx.compose_lib.component.autoRippleAnimation
import com.kiylx.compose_lib.component.rememberRippleAnimationState
import com.kiylx.compose_lib.theme3.DarkThemePrefs.Companion.FOLLOW_SYSTEM
import com.kiylx.compose_lib.theme3.DarkThemePrefs.Companion.OFF
import com.kiylx.compose_lib.theme3.DarkThemePrefs.Companion.ON
import com.kiylx.compose_lib.theme3.LocalDarkThemePrefs
import com.kiylx.compose_lib.theme3.ThemeHelper
import com.kiylx.compose_lib.theme3.ThemeHelper.modifyDarkThemePreference


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DarkThemePreferences(
    window: Window,
    onBackPressed: () -> Unit) {
    val rippleAnimationState = rememberRippleAnimationState()
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
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .autoRippleAnimation(window, rippleAnimationState),
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
            //对比度
            var darkHighContrastProgress by remember {
                mutableFloatStateOf(ThemeHelper.darkThemeHighContrastValue.toFloat())
            }

            LazyColumn(modifier = Modifier.padding(it)) {
                if (Build.VERSION.SDK_INT >= 29) {
                    item {
                        PreferenceSingleChoiceItem(
                            text = stringResource(R.string.follow_system),
                            selected = darkThemePreference.darkThemeMode == FOLLOW_SYSTEM
                        ) {
                            rippleAnimationState.change {
                                scope.modifyDarkThemePreference(FOLLOW_SYSTEM)
                            }
                        }
                    }
                }
                item {
                    PreferenceSingleChoiceItem(
                        text = stringResource(R.string.on),
                        selected = darkThemePreference.darkThemeMode == ON
                    ) {
                        rippleAnimationState.change {
                            scope.modifyDarkThemePreference(ON)
                        }
                    }
                }
                item {
                    PreferenceSingleChoiceItem(
                        text = stringResource(R.string.off),
                        selected = darkThemePreference.darkThemeMode == OFF
                    ) {
                        rippleAnimationState.change {
                            scope.modifyDarkThemePreference(OFF)
                        }
                    }
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
                if (isHighContrastModeEnabled) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            PreferenceSubtitleNotFillWidth(
                                modifier = Modifier.align(Alignment.CenterVertically),
                                contentPadding = PaddingValues(start = 8.dp),
                                text = stringResource(R.string.contrast)
                            )
                            Text(
                                modifier = Modifier
                                    .align(Alignment.Bottom)
                                    .padding(end = 16.dp, top = 8.dp),
                                fontWeight = FontWeight.Bold,
                                text = String.format("%.1f", darkHighContrastProgress)
                            )
                        }
                    }
                    item {
                        //修改暗色模式下的高对比度值
                        Slider(
                            value = darkHighContrastProgress,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            onValueChange = {
                                darkHighContrastProgress = it
                            },
                            valueRange = 0f..1f, onValueChangeFinished = {
                                scope.modifyDarkThemePreference(highContrastValue = darkHighContrastProgress.toDouble())
                            }
                        )
                    }

                }
            }
        })
}