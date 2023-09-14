package com.kiylx.weather.http

import android.app.Application
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.File

/**
 * 给get请求添加缓存-Network层的拦截器
 *
 * 原理：在发起请求的时候，需要设置请求多久之内的缓存数据，或是直接请求网络。而在response设置当前响应可以被存储多久不失效。
 * 而okhttp的缓存拦截器在应用拦截器和网络拦截器之间，所以，需要在应用拦截器设置发起的请求，
 * 是否需要得到缓存以及多久之内的未失效的缓存。以及需要在网络拦截器里设置响应可以被okhttp的缓存拦截器处理时，存储多久
 * 当然，如果请求得到缓存，而得不到或是失效，会得到504的错误。
 *
 * 拦截器依次是: 应用拦截器 - CacheInterceptor - NetworkInterceptor ，
 * Request的cache -Control 需要在CacheInterceptor 之前设定，也就是addInterceptor。
 * 拿到response后， 会反过来，先走NetInterceptor ，再走CacheInterceptor ，最后应用拦截器。
 * 如果想修改response的缓存有效期， 需要addNetInterceptor里执行。
 */
class NetworkCacheInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        // GET请求
        if ("GET" == request.method) {
            //设置响应的可缓存时间
            val cacheTime = request.header(CustomHeader.cacheTime)
            if (cacheTime != null) {
                val response = chain.proceed(request)
                return response.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public, max-age=$cacheTime")
                    .build()
            }
        }

        // 其他类型请求
        return chain.proceed(request)
    }

    companion object {

        fun OkHttpClient.Builder.configCache(ctx: Application) {
            //缓存
            val cacheSize: Long = 20 * 1024 * 1024 //20M
            val okhttpCacheConfig = Cache(
                File(ctx.externalCacheDir!!.absolutePath + File.separator + "http_cache"),
                cacheSize
            )
            cache(okhttpCacheConfig)
            addNetworkInterceptor(NetworkCacheInterceptor())
            addInterceptor(RequestCacheInterceptor())
        }
    }
}

class CustomHeader {
    companion object {
        /**
         * 单位：秒
         * 自定义请求的缓存时间，0秒时对此请求禁用缓存
         */
        const val cacheTime: String = "cache-time"
    }
}

fun Long.minutesToSeconds() = this * 60

