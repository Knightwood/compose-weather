package com.kiylx.weather.repo

import com.kiylx.libx.http.kotlin.basic2.handleApi2
import com.kiylx.libx.http.kotlin.common.RawResponse
import com.kiylx.libx.http.kotlin.common.Retrofit2Holder
import com.kiylx.weather.common.AllPrefs
import com.kiylx.weather.common.DataCacheUtil
import com.kiylx.weather.repo.api.Api
import com.kiylx.weather.repo.bean.DailyEntity
import com.kiylx.weather.repo.bean.Location
import com.kiylx.weather.repo.bean.Location.Companion.toLatLonStr
import com.kiylx.weather.repo.bean.successData
import com.kiylx.weather.repo.local_file.LocalFile

/**
 * 调用和风天气api，并在这里提供数据
 */
object QWeatherRepo {
    val map: HashMap<Location, WeatherSub> = hashMapOf()

    private val api by lazy {
        Retrofit2Holder(AllPrefs.baseUrl).create(Api::class.java)
    }

//<editor-fold desc="网络接口">
    /**
     * 获取实时天气
     * 会查询本地副本
     */
    suspend fun getDailyReport(
        location: Location,
        unit: String? = null,
        lang: String? = null,
    ): RawResponse<DailyEntity> {
        //检查是否需要请求网络获取数据
//        val shouldRequest: Boolean = if (map.containsKey(location)) {
//            如果超过缓存时间，则应该请求网络获取新数据
//            val tmp = map[location]
//            DataCacheUtil.checkIsOutOfDate(tmp?.dailyEntity)
//        } else {
//            true
//        }
        val res = if (location.default) {
            //默认位置，需要使用经纬度获取数据
            handleApi2(api.getGridDaily(location.toLatLonStr(), lang, unit))
        } else {
            handleApi2(api.getDaily(location.id, lang, unit))
        }
        //保存请求结果
//        map[location]?.let {
//            it.dailyEntity = res.successData()
//        } ?: let {
//            //没有location数据，则构建并保存
//            it.map[location] = WeatherSub(
//                location = location,
//                dailyEntity = res.successData()
//            )
//        }
        return res
    }
//</editor-fold>

//<editor-fold desc="从存储库删除天气信息，以及同步删除本地文件">
    /**
     * 删除位置信息时，相应的天气缓存也应该删除
     */
    fun deleteWeather(location: Location) {
        map.remove(location)
        delete(location)
    }

//</editor-fold>

//<editor-fold desc="序列化和反序列化，将本地文件读取到存储库，以及将存储库同步到本地">
    /**
     * 将所有信息保存到磁盘
     */
    fun saveAll() {
        map.values.forEach {
            LocalFile.writeWeather(it)
        }
    }

    /**
     * 将天气信息保存到磁盘
     */
    fun save(data: WeatherSub) {
        LocalFile.writeWeather(data)
    }

    //将位置信息从磁盘读取出来
    fun readAll() {
        LocalFile.readWeather {
            replaceAll(it.toMutableList())
        }
    }

    /**
     * 将传入的天气信息替换原有的天气信息
     */
    fun replaceAll(list: MutableList<WeatherSub>) {
        if (list.isEmpty()) {
            return
        }
        val default = list.find {
            it.location.default
        } ?: throw Exception("default location not found")
        map.clear()
        map[default.location] = default
        list.remove(default)
        for (weatherSub in list) {
            map[weatherSub.location] = weatherSub
        }
    }

    /**
     * 文件删除
     */
    fun delete(location: Location) {
        if (location.default) {
            return
        } else {
            LocalFile.deleteWeather(location)
        }
    }
//</editor-fold>

}