package com.kiylx.weather.ui.activitys

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.kiylx.weather.repo.bean.LocationEntity
import com.kiylx.weather.ui.page.main.WeatherPagerStateHolder
import com.kiylx.weather.ui.page.splash.AddLocationStateHolder
import kotlinx.coroutines.flow.MutableStateFlow

class MainViewModel : ViewModel() {
    /**
     * 添加位置信息页面的状态holder
     */
    val addLocationStateHolder :AddLocationStateHolder by lazy { AddLocationStateHolder() }

    //是否正在执行添加位置信息的行为
    val addLocationActionState :MutableState<Boolean> = mutableStateOf(false)

    /**
     * 持有所有位置信息及对应的dataHolder
     */
    val weatherPageStateHolder: HashMap<LocationEntity, WeatherPagerStateHolder> = hashMapOf()

    /**
     * 每个天气page都需要从这里获取一个StateHolder
     */
    fun getWeatherStateHolder(location: LocationEntity):WeatherPagerStateHolder{
        return weatherPageStateHolder[location]?:let {
            val holder = WeatherPagerStateHolder(location)
            weatherPageStateHolder.putIfAbsent(location,holder)
            holder
        }
    }

}