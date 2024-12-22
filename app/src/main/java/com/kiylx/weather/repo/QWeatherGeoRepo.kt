package com.kiylx.weather.repo

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.kiylx.libx.http.retrofit.RetrofitHolder
import com.kiylx.weather.common.AllPrefs
import com.kiylx.weather.repo.api.GeoApi
import com.kiylx.weather.repo.bean.LocationEntity
import com.kiylx.weather.repo.bean.LocationListEntity
import com.kiylx.weather.repo.local_file.LocalFile

object QWeatherGeoRepo {
    const val TAG = "QWeatherGeoRepo"
    private val api by lazy {
        RetrofitHolder.create(AllPrefs.geoBaseUrl)
            .configRetrofit()
            .createApi(GeoApi::class.java)
    }

    /**
     * gps得到的最新位置信息，全局共享
     */
    var gpsDataState: MutableState<LocationEntity> = mutableStateOf(LocationEntity())

    /**
     * 0下标是默认位置信息 若没有开启gps自动定位更新，则永远保持不变 位置信息的总和
     */
    val allLocationState = mutableStateListOf<LocationEntity>(LocationEntity())

//<editor-fold desc="网络接口">
    /**
     * 通过接口获取地理位置
     */
    suspend fun queryCityList(
        location: String,
        adm: String? = null,
        range: String? = null,
        number: String? = null,
        lang: String? = null,
    ): LocationListEntity? {
        return api.getCity(
            location,
            adm,
            range,
            number,
            lang,
            AllPrefs.hourWeatherInterval
        ).await()
    }
//</editor-fold>

//<editor-fold desc="序列化和反序列化，将本地文件读取到存储库，以及将存储库同步到本地">

    /**
     * 将位置信息保存到磁盘
     */
    fun saveAll() {
        allLocationState.forEachIndexed { index, locationEntity ->
            LocalFile.writeLocation(locationEntity, index)
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
    fun replaceAll(list: MutableList<LocationEntity>) {
        if (list.isEmpty()) {
            return
        }
        allLocationState.clear()
        allLocationState.addAll(list)
        allLocationState.find {
            it.default
        } ?: let {
            //如果找不到默认值，设置一个默认值
            allLocationState[0] = allLocationState[0].copy(default = true)
            LocalFile.writeLocation(allLocationState[0], 0)
        }
    }

//</editor-fold>

//<editor-fold desc="存储库位置信息添加/删除，以及同步本地文件的中间方法">
    /**
     * 添加位置信息
     *
     * @param default 是否添加为默认位置
     * @param data 位置信息
     */
    fun addLocation(locationEntity: LocationEntity, default: Boolean = false) {
        var data: LocationEntity = locationEntity
        if (default) {
            data = data.copy(default = true)
        }
        if (default) {
            if (allLocationState.isEmpty()) {
                allLocationState.add(data)
                LocalFile.writeLocation(data, 0)
            } else {
                if (allLocationState[0] != data) {
                    allLocationState[0] = data
                    LocalFile.writeLocation(data, 0)
                }
            }
        } else {
            val index: Int = allLocationState.indexOf(data)
            if (index == -1) {
                allLocationState.add(data)
                LocalFile.writeLocation(data, allLocationState.lastIndex)
            }
        }
    }

    fun deleteLocation(data: LocationEntity) {
        val index = allLocationState.indexOf(data)
        if (index != -1) {
            allLocationState.remove(data)
            LocalFile.deleteLocation(index)
        }
    }

    /**
     * 从存储库删除位置信息，删除本地文件，删除天气的本地文件
     */
    fun deleteLocation(pos: Int) {
        if (pos > 0) {
            val data = allLocationState.removeAt(pos)
            LocalFile.deleteLocation(pos)
        }
    }

    /**
     * 更新某个坐标
     *
     * @param i allLocationState中的index
     */
    fun update(data: LocationEntity, i: Int) {
        allLocationState[i] = data
        deleteLocation(i)
        LocalFile.writeLocation(data, i)
    }
//</editor-fold>

}