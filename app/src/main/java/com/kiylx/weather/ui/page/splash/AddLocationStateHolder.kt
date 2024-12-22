package com.kiylx.weather.ui.page.splash

import com.kiylx.weather.http.isOK
import com.kiylx.weather.repo.QWeatherGeoRepo
import com.kiylx.weather.repo.bean.LocationListEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AddLocationStateHolder {
    /**
     * gps给与的gpsStr
     */
    val gpsStr: MutableStateFlow<String> = MutableStateFlow("")

    /**
     * api查询得到的位置信息
     */
    private val _location: MutableStateFlow<LocationListEntity?> =
        MutableStateFlow(null)
    val location: StateFlow<LocationListEntity?>
        get() = _location

    /**
     * @param location 以","分割的经纬度字符串或是地名
     * 查询位置信息
     */
    suspend fun getLocation(
        location: String,
    ) {
        val response = QWeatherGeoRepo.queryCityList(location)
        response?.takeIf { it.isOK() }?.let {
            _location.emit(it)
        }
    }

    suspend fun clearLocations() {
        _location.emit(null)
    }
}