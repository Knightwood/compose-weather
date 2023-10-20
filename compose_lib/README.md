# 动态主题

示例：在activity中应用动态主题

```
override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        setContent {
            val navController = rememberNavController()
            DynamicTheme2 {
                HomeEntity(navController = navController)
            }
        }
    }
```

 增加可以跳转到主题设置界面的导航

```
navigation(startDestination = Route.SETTINGS_PAGE, route = Route.SETTINGS) {
。。。
//导航到主题设置页面
animatedComposable(Route.THEME) {
//AppearancePreferences 是库里预定义好的主题设置界面，只需要构建以下导航即可
  AppearancePreferences(navController = navController, navToDarkMode = { ->
        navController.navigate(Route.DARK_THEME)
    })
}
//导航到黑色模式
animatedComposable(Route.DARK_THEME) {
           DarkThemePreferences {
           navController.popBackStack()
     }
}
。。。
}
```

# 主题切换波纹动画

```
@Composable
fun FirstPage(navController: NavController) {
    val scope = rememberCoroutineScope()
    val isDark = LocalDarkThemePrefs.current.isDarkTheme()
    var innerPos = mutableStateOf(Offset.Zero)
    //1. 状态
    val rippleAnimationState = rememberRippleAnimationState {
        animTime = 5000 //这里可以调整一些设置
    }
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            //2.添加modifier，自动获取点击位置
            .autoRippleAnimation(window,rippleAnimationState)
            //2.1 或者使用下面这个，自己手动获取点击位置
            //.rippleAnimation(window,rippleAnimationState)
            //2.2 这个modifier可以获取点击位置
        	//.extendClick {
            //	innerPos.value = it.position
        	//}
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Button(onClick = {
                navController.navigateExt2(Route.SETTINGS, block = {
                    this["arg1"] = "vvv"
                })
            }) {
                Text(text = "前往SecondPage")
            }

            Button(
                modifier = Modifier,
                onClick = {
                    //设置动画效果
                    if (isDark) {
                        rippleAnimationState.animMode = AnimMode.shrink
                    } else {
                        rippleAnimationState.animMode = AnimMode.expend
                    }
                    //3. 调用此方法执行动画，以及切换主题，若是上面选择手动获取点击位置，或是自定义位置，可以调用change方法的时候，传入自己定义好的位置，若是自动获取点击位置，则不用传位置信息
                    rippleAnimationState.change(){
                        //主题切换
                        if (isDark) {
                            scope.modifyDarkThemePreference(darkThemeMode = DarkThemePrefs.OFF)
                        } else {
                            scope.modifyDarkThemePreference(darkThemeMode = DarkThemePrefs.ON)
                        }
                    }
                }) {
                Text(text = "切换主题")
            }

            PressIconButton(
                modifier = Modifier,
                onClick = {},
                icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = null) },
                text = { Text("Add to cart") }
            )
        }
    }
}
```

