package com.kiylx.weather.repo

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.kiylx.libx.http.kotlin.basic2.Resources2
import com.kiylx.libx.http.kotlin.basic2.handleApi2
import com.kiylx.libx.http.kotlin.common.Retrofit2Holder
import com.kiylx.weather.common.AllPrefs
import com.kiylx.weather.repo.api.GeoApi
import com.kiylx.weather.repo.bean.LocationEntity
import com.kiylx.weather.repo.bean.LocationListEntity
import com.kiylx.weather.repo.local_file.LocalFile
import kotlinx.coroutines.flow.MutableStateFlow

object QWeatherGeoRepo {
    const val TAG = "QWeatherGeoRepo"
    private val api by lazy {
        Retrofit2Holder(AllPrefs.geoBaseUrl).create(GeoApi::class.java)
    }

    /**
     * MainActivity查询gps位置信息后，将数据送到这里，使得全局共享
     */
    val gpsStrFlow: MutableStateFlow<String> = MutableStateFlow("")
    var gpsDataState:MutableState<LocationEntity> = mutableStateOf(LocationEntity())

    /**
     * 0下标是默认位置信息
     * 若没有开启gps自动定位更新，则永远保持不变
     * 位置信息的总和
     */
    val allLocationState = mutableStateListOf<LocationEntity>()

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
    ): Resources2<LocationListEntity> {
        return handleApi2(api.getCity(location, adm, range, number, lang,AllPrefs.hourWeatherInterval))
    }
//</editor-fold>

//<editor-fold desc="序列化和反序列化，将本地文件读取到存储库，以及将存储库同步到本地">

    /**
     * 将位置信息保存到磁盘
     */
    fun saveAll() {
        allLocationState.forEach {
            LocalFile.writeLocation(it)
        }
    }

    /**
     * 将位置信息保存到磁盘
     */
    fun save(data: LocationEntity){
        LocalFile.writeLocation(data)
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
        val default = list.find {
            it.default
        } ?: list[0]
        default.default = true//如果找不到默认值，设置一个默认值
        list.remove(default)

        allLocationState.clear()
        allLocationState.add(default)
        allLocationState.addAll(list)
    }

    /**
     * 文件删除
     */
    fun delete(location: LocationEntity) {
        if (location.default) {
            return
        } else {
            allLocationState.remove(location)
            val path = LocalFile.locationDir + LocalFile.genLocationFileName(location)
            LocalFile.deleteFile(path)
        }
    }
//</editor-fold>

//<editor-fold desc="存储库位置信息添加/删除，以及同步本地文件的中间方法">
    /**
     * 添加位置信息
     * @param data 位置信息
     * @param default 是否添加为默认位置
     */
    fun addLocation(data: LocationEntity, default: Boolean = false) {
        data.default = default
        if (default) {
            if (allLocationState.isEmpty()) {
                allLocationState.add(data)
                LocalFile.writeLocation(data)
            } else {
                if (allLocationState[0] != data) {
                    allLocationState[0] = data
                    LocalFile.writeLocation(data)
                }
            }
        } else {
            val b: Boolean = data in allLocationState
            if (!b) {
                allLocationState.add(data)
                LocalFile.writeLocation(data)
            }
        }
    }

    fun deleteLocation(data: LocationEntity) {
        allLocationState.remove(data)
        LocalFile.deleteLocation(data)
    }

    /**
     * 从存储库删除位置信息，删除本地文件，删除天气的本地文件
     */
    fun deleteLocation(pos: Int) {
        if (pos > 0) {
            val data= allLocationState.removeAt(pos)
            LocalFile.deleteLocation(data)
        }
    }
//</editor-fold>

}