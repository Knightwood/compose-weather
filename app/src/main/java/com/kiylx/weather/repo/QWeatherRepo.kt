package com.kiylx.weather.repo

import com.kiylx.libx.http.kotlin.basic2.Resource2
import com.kiylx.libx.http.kotlin.basic2.handleApi2
import com.kiylx.libx.http.kotlin.common.Retrofit2Holder
import com.kiylx.weather.repo.bean.DailyEntity

/**
 * 调用和风天气api，并在这里提供数据
 */
object QWeatherRepo {
    private val api by lazy {
        Retrofit2Holder(QWeatherConf.baseUrl).create(Api::class.java)
    }

    /**
     * 获取实时天气
     * 会查询本地副本
     */
    suspend fun getDailyReport(
        location: String,
        unit: String? = null,
        lang: String? = null,
    ): Resource2<DailyEntity> {
        return handleApi2(api.getDaily(location, lang, unit))
    }

}