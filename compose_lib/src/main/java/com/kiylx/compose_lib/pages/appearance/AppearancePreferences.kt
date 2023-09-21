package com.kiylx.compose_lib.pages.appearance

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.outlined.Contrast
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.navigation.NavHostController
import com.google.android.material.color.DynamicColors
import com.kiylx.compose_lib.R
import com.kiylx.compose_lib.component.BackButton
import com.kiylx.compose_lib.component.LargeTopAppBar
import com.kiylx.compose_lib.component.PreferenceSwitch
import com.kiylx.compose_lib.component.PreferenceSwitchWithDivider
import com.kiylx.compose_lib.component.VideoCard
import com.kiylx.compose_lib.theme3.DarkThemePrefs
import com.kiylx.compose_lib.theme3.LocalColorScheme
import com.kiylx.compose_lib.theme3.LocalDarkThemePrefs
import com.kiylx.compose_lib.theme3.LocalDynamicColorSwitch
import com.kiylx.compose_lib.theme3.LocalPaletteStyleIndex
import com.kiylx.compose_lib.theme3.LocalSeedColor
import com.kiylx.compose_lib.theme3.PaletteStyle
import com.kiylx.compose_lib.theme3.ThemeHelper
import com.kiylx.compose_lib.theme3.ThemeHelper.modifyDarkThemePreference
import com.kiylx.compose_lib.theme3.ThemeHelper.modifyThemeSeedColor
import com.kiylx.compose_lib.theme3.ThemeHelper.recoveryDefaultTheme
import com.kiylx.compose_lib.theme3.ThemeHelper.switchDynamicColor
import com.kiylx.compose_lib.theme3.mDynamicColorScheme
import com.kyant.m3color.hct.Hct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


// 外观页面，动态主题使用的https://github.com/Kyant0/m3color

val colorList = ((0..11).map { it*30.0 }) .map { Color(Hct.from(it, 35.0, 40.0).toInt()) }

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class
)
@Composable
fun AppearancePreferences(
    navController: NavHostController,
    navToDarkMode: () -> Unit
) {
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

    Scaffold(modifier = Modifier
        .fillMaxSize()
        .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(title = {
                Text(
                    modifier = Modifier,
                    text = stringResource(id = R.string.display),
                )
            }, navigationIcon = {
                BackButton {
                    navController.popBackStack()
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
                HorizontalPager(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clearAndSetSemantics { },
                    state = pagerState,
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) { pageIndex ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) { ColorButtons(colorList[pageIndex], scope) }
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
                        isChecked = LocalDynamicColorSwitch.current,
                        onClick = {
                            scope.launch {
                                switchDynamicColor()
                            }
                        })
                }
                val isDarkTheme = LocalDarkThemePrefs.current.isDarkTheme()
                PreferenceSwitchWithDivider(title = stringResource(id = R.string.dark_theme),
                    icon = if (isDarkTheme) Icons.Outlined.DarkMode else Icons.Outlined.LightMode,
                    isChecked = isDarkTheme,
                    description = LocalDarkThemePrefs.current.getDarkThemeDesc(),
                    onChecked = {
                        scope.modifyDarkThemePreference(if (it) DarkThemePrefs.ON else DarkThemePrefs.OFF)

                    },
                    onClick = { navToDarkMode() })

                var useDefaultThemeChecked by remember {
                  mutableStateOf(ThemeHelper.useDefaultTheme)
                }
                PreferenceSwitch(
                    title = stringResource(R.string.use_default_theme),
                    icon = Icons.Outlined.Contrast,
                    isChecked = useDefaultThemeChecked, onClick = {
                        useDefaultThemeChecked=it
                        scope.recoveryDefaultTheme(useDefaultTheme = it)
                    }
                )

            }
        })
}

/**
 * 对于一种颜色种子，生成了多种不同的风格
 */
@Composable
fun RowScope.ColorButtons(color: Color, scope: CoroutineScope) {
    listOf<PaletteStyle>(
        PaletteStyle.TonalSpot,
        PaletteStyle.Expressive,
        PaletteStyle.FruitSalad,
        PaletteStyle.Rainbow
    ).forEachIndexed { index, style: PaletteStyle ->
        ColorButton(color = color, index = index, tonalStyle = style, scope = scope)
    }
}

@Composable
fun RowScope.ColorButton(
    modifier: Modifier = Modifier,
    color: Color = Color.Green,
    index: Int = 0,
    tonalStyle: PaletteStyle = PaletteStyle.TonalSpot,
    scope: CoroutineScope,
) {
    val tonalPalettes by remember {
        mutableStateOf(
            mDynamicColorScheme(
                color,
                false,
                tonalStyle,
                ThemeHelper.lightThemeHighContrastValue
            )
        )
    }
    val isSelect =
        !LocalDynamicColorSwitch.current
                && LocalSeedColor.current == color.toArgb()
                && LocalPaletteStyleIndex.current == index
    ColorButtonImpl(modifier = modifier, colorScheme = tonalPalettes, isSelected = { isSelect }) {
        scope.switchDynamicColor(enabled = false)
        scope.modifyThemeSeedColor(color.toArgb(), index)
        scope.recoveryDefaultTheme(useDefaultTheme = false)
    }

}

@Composable
fun RowScope.ColorButtonImpl(
    modifier: Modifier = Modifier,
    isSelected: () -> Boolean = { false },
    colorScheme: ColorScheme,
    onClick: () -> Unit = {}
) {
    val containerColor: Color = LocalColorScheme.current.primaryContainer
    val containerSize by animateDpAsState(targetValue = if (isSelected.invoke()) 28.dp else 0.dp)
    val iconSize by animateDpAsState(targetValue = if (isSelected.invoke()) 16.dp else 0.dp)

    Surface(
        modifier = modifier
            .padding(4.dp)
            .sizeIn(maxHeight = 80.dp, maxWidth = 80.dp, minHeight = 64.dp, minWidth = 64.dp)
            .weight(1f, false)
            .aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
        color = LocalColorScheme.current.tertiaryContainer,
        onClick = onClick
    ) {
        CompositionLocalProvider(LocalColorScheme provides colorScheme) {
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
                            .drawBehind { drawCircle(containerColor) },
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = null,
                            modifier = Modifier
                                .size(iconSize)
                                .align(Alignment.Center),
                            tint = LocalColorScheme.current.onPrimaryContainer
                        )
                    }

                }
            }
        }
    }
}