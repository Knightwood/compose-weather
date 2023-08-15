package com.kiylx.weather.repo

import com.kiylx.libx.http.kotlin.basic2.Resource2
import com.kiylx.libx.http.kotlin.basic2.handleApi2
import com.kiylx.libx.http.kotlin.common.Retrofit2Holder
import com.kiylx.weather.common.AllPrefs
import com.kiylx.weather.repo.api.GeoApi
import com.kiylx.weather.repo.bean.Location
import com.kiylx.weather.repo.bean.LocationListEntity
import com.kiylx.weather.repo.local_file.LocalFile
import kotlinx.coroutines.flow.MutableStateFlow

object QWeatherGeoRepo {
    const val TAG = "QWeatherGeoRepo"

    /**
     * MainActivity查询gps位置信息后，将数据送到这里，使得全局共享
     */
    val gpsDataFlow: MutableStateFlow<String> = MutableStateFlow("")

    private val api by lazy {
        Retrofit2Holder(AllPrefs.geoBaseUrl).create(GeoApi::class.java)
    }

    /**
     * 0下标是默认位置信息，即当前位置
     * 若没有开启gps自动定位更新，则永远保持不变
     * 位置信息的总和
     */
    val allLocations: MutableList<Location> = mutableListOf()

    val allLocationsFlow: MutableStateFlow<MutableList<Location>> =
        MutableStateFlow(allLocations)

    /**
     * 将位置信息保存到磁盘
     */
    fun saveAll() {
        allLocations.forEach {
            LocalFile.writeLocation(it)
        }
    }

    //将位置信息从磁盘读取出来
    fun readAll() {
        LocalFile.readLocations {
            replaceAll(it.toMutableList())
        }
    }

    /**
     * 将传入的位置信息替换原有的位置信息
     */
    fun replaceAll(list: MutableList<Location>) {
        if (list.isEmpty()) {
            return
        }
        val default = list.find {
            it.default
        } ?: list[0]
        default.default = true//如果找不到默认值，设置一个默认值
        list.remove(default)
        allLocations.clear()
        allLocations.add(default)
        allLocations.addAll(list)
        allLocationsFlow.tryEmit(allLocations)
    }

    /**
     * 文件删除
     */
    fun delete(location: Location) {
        if (location.default) {
            return
        } else {
            allLocations.remove(location)
            allLocationsFlow.tryEmit(allLocations)
            val path = LocalFile.locationDir + location.id + LocalFile.locationSuffix
            LocalFile.deleteFile(path)
        }
    }

    /**
     * 通过接口获取地理位置
     */
    suspend fun queryCityList(
        location: String,
        adm: String? = null,
        range: String? = null,
        number: String? = null,
        lang: String? = null,
    ): Resource2<LocationListEntity> {
        return handleApi2(api.getCity(location, adm, range, number, lang))
    }

    /**
     * 添加位置信息
     * @param data 位置信息
     * @param default 是否添加为默认位置
     */
    fun addLocation(data: Location, default: Boolean = false) {
        data.default = default
        if (default) {
            if (allLocations.isEmpty()) {
                allLocations.add(data)
            } else {
                allLocations[0] = data
            }
        } else {
            val b: Boolean = data in allLocations
            if (!b) {
                allLocations.add(data)
            }
        }
        allLocationsFlow.tryEmit(allLocations)
    }

    fun deleteLocation(data: Location) {
        allLocations.remove(data)
        allLocationsFlow.tryEmit(allLocations)
    }

    fun deleteLocation(pos: Int) {
        if (pos > 0) {
            allLocations.removeAt(pos)
            allLocationsFlow.tryEmit(allLocations)
        }
    }


}