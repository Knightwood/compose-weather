package com.kiylx.compose_lib.pages.appearance

import android.view.Window
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.Contrast
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import com.google.android.material.color.DynamicColors
import com.kiylx.compose_lib.R
import com.kiylx.compose_lib.component.BackButton
import com.kiylx.compose_lib.component.LargeTopAppBar
import com.kiylx.compose_lib.component.PreferenceSwitch
import com.kiylx.compose_lib.component.PreferenceSwitchWithDivider
import com.kiylx.compose_lib.component.PreferenceTitle
import com.kiylx.compose_lib.component.RippleAnimationState
import com.kiylx.compose_lib.component.VideoCard
import com.kiylx.compose_lib.component.autoRippleAnimation
import com.kiylx.compose_lib.component.rememberRippleAnimationState
import com.kiylx.compose_lib.theme3.DarkThemePrefs
import com.kiylx.compose_lib.theme3.LocalColorScheme
import com.kiylx.compose_lib.theme3.LocalDarkThemePrefs
import com.kiylx.compose_lib.theme3.LocalIsUseDynamicColor
import com.kiylx.compose_lib.theme3.LocalPaletteStyleIndex
import com.kiylx.compose_lib.theme3.LocalSeedColor
import com.kiylx.compose_lib.theme3.PaletteStyle
import com.kiylx.compose_lib.theme3.ThemeHelper
import com.kiylx.compose_lib.theme3.ThemeHelper.modifyDarkThemePreference
import com.kiylx.compose_lib.theme3.ThemeHelper.modifyThemeSeedColor
import com.kiylx.compose_lib.theme3.ThemeHelper.recoveryDefaultTheme
import com.kiylx.compose_lib.theme3.ThemeHelper.switchDynamicColor
import com.kiylx.compose_lib.theme3.ThemeSettings
import com.kiylx.compose_lib.theme3.mDynamicColorScheme
import com.kyant.m3color.hct.Hct
import kotlinx.coroutines.CoroutineScope

val colorList = ((0..11).map { it * 31.0 }).map { Color(Hct.from(it, 45.0, 45.0).toInt()) }

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalLayoutApi::class
)
@Composable
fun AppearancePreferences(
    back:()->Unit,
    navToDarkMode: () -> Unit,
    window: Window,
) {
    val themeSettingState = ThemeHelper.AppSettingsStateFlow.collectAsState()
    val themeSetting = themeSettingState.value
    val isDarkTheme = themeSetting.darkTheme.isDarkTheme()
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState(),
            canScroll = { true })
    val image by remember {
        mutableIntStateOf(
            listOf(
                R.drawable.sample, R.drawable.sample1, R.drawable.sample2, R.drawable.sample3
            ).random()
        )
    }
    val scope = rememberCoroutineScope()
    val rippleAnimationState = rememberRippleAnimationState()
    Scaffold(modifier = Modifier
        .fillMaxSize()
        .nestedScroll(scrollBehavior.nestedScrollConnection)
        .autoRippleAnimation(window, rippleAnimationState),
        topBar = {
            LargeTopAppBar(title = {
                Text(
                    modifier = Modifier,
                    text = stringResource(id = R.string.display),
                )
            }, navigationIcon = {
                BackButton {
                    back.invoke()
                }
            }, scrollBehavior = scrollBehavior
            )
        },
        content = {
            Column(
                Modifier
                    .padding(it)
                    .verticalScroll(rememberScrollState())
            ) {

                VideoCard(
                    modifier = Modifier.padding(18.dp), thumbnailUrl = image
                )
                val pageCount = colorList.size

                val pagerState =
                    rememberPagerState(
                        initialPage = colorList.indexOf(
                            Color(LocalSeedColor.current)
                        ).run { if (this == -1) 0 else this },
                        pageCount = {
                            pageCount
                        })

                PreferenceTitle(
                    title = stringResource(R.string.theme_color),
                    icon = Icons.Outlined.ColorLens
                )

                HorizontalPager(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clearAndSetSemantics { },
                    state = pagerState,
                ) { pageIndex ->
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        maxItemsInEachRow = 4,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        ColorButtons(
                            color = colorList[pageIndex],
                            scope = scope,
                            rippleAnimationState = rippleAnimationState,
                            themeState = themeSettingState
                        )
                    }
                }
                Row(
                    Modifier
                        .height(50.dp)
                        .padding(top = 4.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(pageCount) { iteration ->
                        val color =
                            if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(color)
                                .size(10.dp)

                        )
                    }
                }

                if (DynamicColors.isDynamicColorAvailable()) {
                    PreferenceSwitch(title = stringResource(id = R.string.dynamic_color),
                        description = stringResource(
                            id = R.string.dynamic_color_desc
                        ),
                        icon = Icons.Outlined.Palette,
                        isChecked = LocalIsUseDynamicColor.current,
                        onClick = {
                            rippleAnimationState.change {
                                scope.switchDynamicColor()
                            }
                        })
                }
                PreferenceSwitchWithDivider(title = stringResource(id = R.string.dark_theme),
                    icon = if (isDarkTheme) Icons.Outlined.DarkMode else Icons.Outlined.LightMode,
                    isChecked = isDarkTheme,
                    description = LocalDarkThemePrefs.current.getDarkThemeDesc(),
                    onChecked = {
                        rippleAnimationState.change {
                            scope.modifyDarkThemePreference(if (it) DarkThemePrefs.ON else DarkThemePrefs.OFF)
                        }

                    },
                    onClick = { navToDarkMode() })
                var useDefaultThemeChecked = themeSetting.useDefaultTheme
                PreferenceSwitch(
                    title = stringResource(R.string.use_default_theme),
                    icon = Icons.Outlined.Contrast,
                    isChecked = useDefaultThemeChecked, onClick = {
                        rippleAnimationState.change {
                            useDefaultThemeChecked = it
                            scope.recoveryDefaultTheme(useDefaultTheme = it)
                        }
                    }
                )

            }
        })
}

/**
 * 对于一种颜色种子，生成了多种不同的风格
 */
@Composable
fun RowScope.ColorButtons(
    color: Color,
    scope: CoroutineScope,
    rippleAnimationState: RippleAnimationState,
    themeState: State<ThemeSettings>
) {
    val isDark = themeState.value.darkTheme.isDarkTheme()
    val contrastValue = ThemeHelper.currentThemeContrastValue()
    val defaultColor = LocalColorScheme.current
    listOf<PaletteStyle>(
        PaletteStyle.TonalSpot,
        PaletteStyle.Neutral,
        PaletteStyle.Vibrant,
        PaletteStyle.Expressive,
        PaletteStyle.Rainbow,
        PaletteStyle.FruitSalad,
    ).forEachIndexed { index, style: PaletteStyle ->
        ColorButton(
            color = color,
            index = index,
            tonalStyle = style,
            scope = scope,
            isDark = isDark,
            contrastValue = contrastValue,
            rippleAnimationState = rippleAnimationState,
            defaultColor = defaultColor,
        )
    }
}

@Composable
fun RowScope.ColorButton(
    modifier: Modifier = Modifier,
    color: Color = Color.Green,
    index: Int = 0,
    tonalStyle: PaletteStyle = PaletteStyle.TonalSpot,
    scope: CoroutineScope,
    isDark: Boolean,
    contrastValue: Double,
    rippleAnimationState: RippleAnimationState,
    defaultColor: ColorScheme,
) {
    var tonalPalettes by remember {
        mutableStateOf(defaultColor)
    }
    LaunchedEffect(key1 = isDark, key2 = contrastValue, block = {
        tonalPalettes = mDynamicColorScheme(
            color,
            isDark,
            tonalStyle,
            contrastValue
        )
    })
    val isSelect =
        !LocalIsUseDynamicColor.current
                && LocalSeedColor.current == color.toArgb()
                && LocalPaletteStyleIndex.current == index
    ColorButtonImpl(modifier = modifier, colorScheme = tonalPalettes, isSelected = { isSelect }) {
        rippleAnimationState.change {
            scope.switchDynamicColor(enabled = false)
            scope.modifyThemeSeedColor(color.toArgb(), index)
            scope.recoveryDefaultTheme(useDefaultTheme = false)
        }
    }

}

@Composable
fun RowScope.ColorButtonImpl(
    modifier: Modifier = Modifier,
    isSelected: () -> Boolean = { false },
    colorScheme: ColorScheme,
    onClick: () -> Unit = {}
) {
    val containerSize by animateDpAsState(
        targetValue = if (isSelected.invoke()) 28.dp else 0.dp,
        label = "ColorButtonContainerSize"
    )
    val iconSize by animateDpAsState(
        targetValue = if (isSelected.invoke()) 16.dp else 0.dp,
        label = "ColorButtonIconSize"
    )

    Surface(
        modifier = modifier
            .padding(4.dp)
            .sizeIn(maxHeight = 80.dp, maxWidth = 80.dp, minHeight = 64.dp, minWidth = 64.dp)
            .weight(1f, false)
            .aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
        color =MaterialTheme.colorScheme.tertiaryContainer,
        onClick = onClick
    ) {
        val color1 = colorScheme.primary
        val color2 = colorScheme.secondaryContainer
        val color3 = colorScheme.tertiary
        Box(Modifier.fillMaxSize()) {
            Box(modifier = modifier
                .size(48.dp)
                .clip(CircleShape)
                .drawBehind { drawCircle(color1) }
                .align(Alignment.Center)) {
                Surface(
                    color = color2, modifier = Modifier
                        .align(Alignment.BottomStart)
                        .size(24.dp)
                ) {}
                Surface(
                    color = color3, modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(24.dp)
                ) {}
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .clip(CircleShape)
                        .size(containerSize)
                        .drawBehind { drawCircle(colorScheme.primaryContainer) },
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = null,
                        modifier = Modifier
                            .size(iconSize)
                            .align(Alignment.Center),
                        tint = colorScheme.onPrimaryContainer
                    )
                }

            }
        }
    }
}