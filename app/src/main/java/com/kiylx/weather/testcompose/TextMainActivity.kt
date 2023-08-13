package com.kiylx.weather.testcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.kiylx.weather.icon.WeatherIcon
import com.kiylx.weather.ui.theme.WeatherTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.kiylx.weather.icon.NoImage

/**
 * 这是一个包括了底部导航，侧边抽屉，顶部栏的示例
 */
class TextMainActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WeatherIcon.init(this.application)
        setContent {
            WeatherTheme {
//                val navController = rememberAnimatedNavController()
//                val bottomBarState = rememberSaveable { (mutableStateOf(true)) }
//                val systemBarFollowThemeState = rememberSaveable { (mutableStateOf(true)) }
//                val systemUiController = rememberSystemUiController()
//                systemUiController.systemBarsDarkContentEnabled =
//                    systemBarFollowThemeState.value && !isSystemInDarkTheme()
                AppScaffold()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AppScaffold() {
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        var selectedItem by remember { mutableStateOf(0) }//底部选择的item
        val items = listOf("主页", "我喜欢的", "设置")

        val drawerState = rememberDrawerState(DrawerValue.Closed)//侧栏状态
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerShape = RoundedCornerShape(8.dp),
                    drawerTonalElevation = 4.dp,
                    content = {
                        //抽屉打开的时候返回，不退出app，而是把抽屉关上
                        BackHandler(enabled = drawerState.isOpen) {
                            scope.launch {
                                drawerState.close()
                            }
                        }
                        Row(Modifier.padding(8.dp)) {
                            Icon(
                                Icons.NoImage, contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "测试用户",
                                modifier = Modifier.align(Alignment.CenterVertically),
                                fontStyle = FontStyle.Italic,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                )
            }) {
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    TopAppBar(
                        scrollBehavior = scrollBehavior,
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        drawerState.open()
                                        snackbarHostState.showSnackbar("open msg")
                                    }
                                }
                            ) {
                                Icon(Icons.Filled.Menu, null)
                            }
                        },
                        title = {
                            Text("魔卡沙的炼金工坊")
                        }
                    )
                },
                bottomBar = {
                    NavigationBar() {
                        items.forEachIndexed { index, item ->
                            NavigationBarItem(
                                alwaysShowLabel = false,
                                icon = {
                                    when (index) {
                                        0 -> Icon(Icons.Filled.Home, contentDescription = null)
                                        1 -> Icon(Icons.Filled.Favorite, contentDescription = null)
                                        else -> Icon(
                                            Icons.Filled.Settings,
                                            contentDescription = null
                                        )
                                    }
                                },
                                label = { Text(item) },
                                selected = selectedItem == index,
                                onClick = { selectedItem = index }
                            )
                        }

                    }
                }, content = { it: PaddingValues ->
                    //主页面内容
                    val item = items[selectedItem]
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it)
                    ) {
                        Text(
                            "当前界面是 $item",
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                        )
                        if (selectedItem == 1) {
                            AList()
                        }
                    }

                }
            )
        }

    }

}