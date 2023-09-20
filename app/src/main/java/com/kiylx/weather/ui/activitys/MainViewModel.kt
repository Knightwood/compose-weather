package com.kiylx.weather.ui.activitys

import androidx.lifecycle.ViewModel
import com.kiylx.weather.repo.bean.LocationEntity
import com.kiylx.weather.ui.page.main.WeatherPagerStateHolder

class MainViewModel : ViewModel() {
    val weatherPageStateHolder: HashMap<LocationEntity, WeatherPagerStateHolder> = hashMapOf()
    fun getWeatherStateHolder(location: LocationEntity):WeatherPagerStateHolder{
        return weatherPageStateHolder[location]?:let {
            val holder = WeatherPagerStateHolder(location)
            weatherPageStateHolder.putIfAbsent(location,holder)
            holder
        }
    }

}