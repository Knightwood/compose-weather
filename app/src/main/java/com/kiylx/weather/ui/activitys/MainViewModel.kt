package com.kiylx.weather.ui.activitys

import androidx.lifecycle.ViewModel
import com.kiylx.weather.repo.bean.Location
import com.kiylx.weather.ui.page.main.WeatherPagerStateHolder

class MainViewModel : ViewModel() {
    val weatherPageStateHolder: HashMap<Location, WeatherPagerStateHolder> = hashMapOf()
    fun getWeatherStateHolder(location: Location):WeatherPagerStateHolder{
        return weatherPageStateHolder[location]?:let {
            val holder = WeatherPagerStateHolder(location)
            weatherPageStateHolder.putIfAbsent(location,holder)
            holder
        }
    }

}