package com.kiylx.weather.http

import com.blankj.utilcode.util.NetworkUtils
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.TimeUnit

/**
 * 给get请求添加缓存
 * 应用层的拦截器，无论有无网络，都优先返回缓存
 */
class RequestCacheInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        //网络不可用时一直使用缓存
        if (!NetworkUtils.isConnected()) {
            val newRequest: Request = request
                .newBuilder()
                .removeHeader("Pragma")
                .header("Cache-Control", "public, only-if-cached")
                .build()
            return chain.proceed(newRequest)
        } else {
            //有网络时设置请求有效范围内的缓存
            val cacheTime = request.header(CustomHeader.cacheTime)//单位：秒
            return if (cacheTime != null) {
                val newRequest: Request = request
                    .newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public, max-age=$cacheTime")
                    .build()
                chain.proceed(newRequest)
            } else {
                chain.proceed(request)
            }
        }
    }
}