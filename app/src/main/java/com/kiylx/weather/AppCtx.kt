package com.kiylx.weather

import android.app.Application
import android.util.Log
import com.kiylx.libx.http.kotlin.common.OkhttpClientProvider
import com.kiylx.libx.http.okhttp_logger.Level
import com.kiylx.libx.http.okhttp_logger.LoggingInterceptor
import com.kiylx.weather.http.KeyInterceptor
import com.kiylx.weather.http.NetworkCacheInterceptor.Companion.configCache
import com.kiylx.weather.icon.WeatherIcon
import com.kiylx.weather.repo.QWeatherGeoRepo
import com.kiylx.weather.repo.QWeatherRepo
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.plus
import okhttp3.Dispatcher

class AppCtx : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        scope = CoroutineScope(Dispatchers.IO) + SupervisorJob() + CoroutineName("GLOAB")
        //mmkv初始化
        MMKV.initialize(this)
        //图标加载
        WeatherIcon.init(this)
        //配置OKHttpClient
        OkhttpClientProvider.configOkHttpClient {
            val dispatcher = Dispatcher()
            dispatcher.maxRequests = 3
            dispatcher(dispatcher)
            configCache(this@AppCtx)//配置缓存策略
            var loggerInterceptor: LoggingInterceptor? = null
            val isDebug = BuildConfig.DEBUG
            if (isDebug) {
                loggerInterceptor = LoggingInterceptor.Builder()
                    .setLevel(Level.BASIC)
                    .log(Log.VERBOSE)
                    .singleTag(true)
                    .tag("tty0-HttpLogger")
                    .build()
            }
            if (isDebug) {//debug模式添加日志打印
                loggerInterceptor?.let {
                    addInterceptor(it)
                }
            }
            //请求带上key
            addInterceptor(KeyInterceptor())
        }
        QWeatherGeoRepo.readAll()//读取本地存储的位置信息
    }

    companion object {
        lateinit var instance: AppCtx
        lateinit var scope: CoroutineScope
    }
}