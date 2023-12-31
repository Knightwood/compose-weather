package com.kiylx.weather.ui.page.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kiylx.weather.repo.bean.LocationEntity
import com.kiylx.weather.ui.activitys.MainViewModel
import kotlinx.coroutines.launch


/**
 * 请求定位权限,进行定位，得到默认位置
 * 或是搜索添加位置。
 */
@Composable
fun AddLocationPage(
    queryGps: () -> Unit,
    stopGps: () -> Unit,
    mainViewModel: MainViewModel,
    complete: (location: LocationEntity) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val locationStateHolder = remember {
        AddLocationStateHolder()
    }

    //api得到的位置信息数据
    val locations = locationStateHolder.location.collectAsState()
    //输入框内容
    val input = remember {
        mutableStateOf("")
    }

    //监听gps定位数据
    LaunchedEffect(Unit, block = {
        mainViewModel.addLocationStateHolder.gpsStr.collect {
            if (it.isNotEmpty()) {
                mainViewModel.addLocationActionState.value = false//标记当前不需要获取gps信息
                stopGps()
                input.value = it
                locationStateHolder.getLocation(it)
            }
        }
    })

    //页面
    Box(
        modifier = Modifier
            .padding(8.dp).fillMaxSize().systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .matchParentSize()
                .padding(8.dp),
        ) {
            Text(
                modifier = Modifier
                    .align(Alignment.End)
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                text = "选择你的位置",
                style = MaterialTheme.typography.displayMedium,
                textAlign = TextAlign.End
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = input.value, onValueChange = {
                    input.value = it
                    //查询地址
                    scope.launch {
                        if (it.isNotEmpty() && it.isNotBlank()) {
                            locationStateHolder.getLocation(it)
                        } else {
                            locationStateHolder.clearLocations()
                        }
                    }
                }, label = { Text("位置查询或定位") })
            ElevatedButton(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.End), onClick = {
                    mainViewModel.addLocationActionState.value = true//标记当前需要获取gps信息
                    queryGps()//自动定位查询
                }) {
                Icon(
                    imageVector = Icons.Rounded.LocationOn,
                    contentDescription = "定位"
                )
                Text(text = "获取定位")
            }
            LazyColumn(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {

                locations.value?.data?.apply {
                    items(this) {
                        Surface(modifier = Modifier.padding(bottom = 8.dp),
                            onClick = {
                                //点击某个item后，设置默认位置，并跳转到主页面
                                complete(it)
                            }) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        MaterialTheme.colorScheme.secondaryContainer,
                                        RoundedCornerShape(4.dp)
                                    )
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically)
                                        .padding(start = 4.dp)
                                        .size(36.dp),
                                    imageVector = Icons.Filled.LocationCity,
                                    contentDescription = null
                                )
                                Column(modifier = Modifier.padding(start = 4.dp)) {
                                    Text(
                                        modifier = Modifier.padding(4.dp),
                                        text = "${it.name}-${it.adm2}"
                                    )
                                    Text(
                                        modifier = Modifier.padding(
                                            start = 4.dp,
                                            bottom = 4.dp,
                                            end = 4.dp
                                        ), text = "${it.adm1}-${it.country}"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

