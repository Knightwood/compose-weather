package com.kiylx.weather.repo

import com.kiylx.libx.http.kotlin.basic2.Resource2
import com.kiylx.libx.http.kotlin.basic2.handleApi2
import com.kiylx.libx.http.kotlin.common.Retrofit2Holder
import com.kiylx.weather.common.AllPrefs
import com.kiylx.weather.common.DataCacheUtil
import com.kiylx.weather.repo.api.Api
import com.kiylx.weather.repo.bean.DailyEntity
import com.kiylx.weather.repo.bean.Location
import com.kiylx.weather.repo.bean.Location.Companion.toLatLonStr
import com.kiylx.weather.repo.local_file.LocalFile

/**
 * 调用和风天气api，并在这里提供数据
 * 每隔10分钟，将数据写入一次文件
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
    ): Resource2<DailyEntity> {
        //检查是否需要请求网络获取数据
        val shouldRequest:Boolean = if (map.containsKey(location)) {
            //如果超过缓存时间，则应该请求网络获取新数据
            val tmp = map[location]
            DataCacheUtil.checkIsOutOfDate(tmp?.dailyEntity)
        } else {
            true
        }
        return if (shouldRequest) {
            if (location.default) {
                //默认位置，需要使用经纬度获取数据
                handleApi2(api.getGridDaily(location.toLatLonStr(), lang, unit))
            } else {
                handleApi2(api.getDaily(location.id, lang, unit))
            }
        } else {
            Resource2.Success(map[location]!!.dailyEntity)
        }

    }
//</editor-fold>

//<editor-fold desc="从存储库添加/删除/更新天气信息，以及本地文件操作的中间方法">

    fun deleteWeather(data: Location) {
        LocalFile.deleteWeather(data)
    }
//</editor-fold>


}