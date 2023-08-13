package com.kiylx.weather

import android.app.Application
import android.util.Log
import com.blankj.utilcode.util.AppUtils
import com.kiylx.libx.http.kotlin.common.OkhttpClientProvider
import com.kiylx.libx.http.okhttp_logger.Level
import com.kiylx.libx.http.okhttp_logger.LoggingInterceptor
import com.kiylx.weather.http.KeyInterceptor
import com.kiylx.weather.icon.WeatherIcon
import com.tencent.mmkv.MMKV
import okhttp3.Dispatcher

class AppCtx : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        //mmkv初始化
        MMKV.initialize(this)
        //图标加载
        WeatherIcon.init(this)
        //配置OKHttpClient
        OkhttpClientProvider.configOkHttpClient {
            val dispatcher = Dispatcher()
            dispatcher.maxRequests = 3
            dispatcher(dispatcher)

            var loggerInterceptor: LoggingInterceptor? = null
            val isDebug = AppUtils.isAppDebug()
            if (isDebug) {
                loggerInterceptor = LoggingInterceptor.Builder()
                    .setLevel(Level.BASIC)
                    .log(Log.VERBOSE)
                    .singleTag(true)
                    .tag("tty1-HttpLogger")
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
    }

    companion object {
        lateinit var instance: AppCtx
    }
}