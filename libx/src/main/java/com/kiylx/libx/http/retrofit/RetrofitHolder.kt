package com.kiylx.libx.http.retrofit

import com.kiylx.libx.http.okhttp3.OkhttpClientProvider
import com.kiylx.libx.http.okhttp3.body.Http3
import com.kiylx.libx.http.okhttp3.call_adapter.ApiResultCallAdapterFactory
import com.kiylx.libx.http.okhttp3.call_adapter.DeferredApiResultCallAdapterFactory
import com.kiylx.libx.http.okhttp3.call_adapter.DeferredCallAdapterFactory
import com.kiylx.libx.http.okhttp3.call_adapter.FlowCallAdapterFactory
import com.kiylx.libx.http.okhttp3.call_adapter.NullOnEmptyConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

/**
 * 使用方式：
 *
 * ```kotlin
 * //生成单例并进行配置
 * val holder=RetrofitHolder.create(baseUrl, jsonUtil, true)
 *             .configOkHttpClient()//配置okhttpClient，还可以在OkhttpClientProvider.configOkHttpClient()中配置
 *             .configRetrofit()//配置retrofit
 * //创建api
 * val mainApi =holder.create(Api::class.java)
 *
 * //单例模式下获取已创建好的RetrofitHolder实例：
 * val holder=RetrofitHolder.getInstance()
 *
 * ```
 */
open class RetrofitHolder private constructor(
    private val baseUrl: String,
    private var json: Json,
) {
    private var contentType: String = Http3.ContentType.application_json_utf8
    private var mOkHttpClient: OkHttpClient? = null
    private var mRetrofit: Retrofit? = null
//        get() {
//            if (field == null)
//                throw IllegalAccessException("请调用configRetrofit配置Retrofit2")
//            return field
//        }
    /**
     * 如果已经调用[OkhttpClientProvider.configOkHttpClient]进行全局配置，则此方法可以不调用。
     *
     * 如果[mOkHttpClient]存在：直接返回，不进行配置。
     *
     * 如果[mOkHttpClient]不存在:
     *
     *      1.newInstance==true:创建新的实例并进行配置。
     *      2.newInstance==false:使用OkhttpClientProvider中的okHttpClient实例。
     *      如果OkhttpClientProvider中的okHttpClient实例存在，则不对其进行重新配置。
     *      若要重新配置，使用[OkhttpClientProvider.configOkHttpClient]
     *
     * @param newInstance 是否创建新的okHttpClient实例， true:创建新的实例并进行配置。
     *    false:使用OkhttpClientProvider中提供的默认值
     *    如果你使用单例模式，此参数最好设置为false，避免重复生成okHttpClient实例。
     */
    fun configOkHttpClient(
        newInstance: Boolean = false,
        block: OkHttpClient.Builder.() -> Unit = {}
    ): RetrofitHolder {
        this.mOkHttpClient ?: let {
            if (newInstance) {
                val newInstanceBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
                newInstanceBuilder.block()
                mOkHttpClient = newInstanceBuilder.build()
            } else {
                mOkHttpClient = OkhttpClientProvider.configOkHttpClient(false, block)
            }
        }
        return this
    }

    /**
     * 需要在[configOkHttpClient]之后调用
     *
     * 如果[mRetrofit]已存在，直接返回。如果[mRetrofit]不存在，则创建新的实例并进行配置。
     *
     * 如果[mOkHttpClient]不存在，则使用[OkhttpClientProvider]中的[OkhttpClientProvider.okHttpClient]。
     * 你最好在此之前就配置了[OkhttpClientProvider]中的[OkhttpClientProvider.okHttpClient]。
     */
    fun configRetrofit(block: Retrofit.Builder.() -> Unit = {}): RetrofitHolder {
        if (mRetrofit == null) {
            //HttpData<*>为后端服务返回的数据结构
            //支持 ApiResult<HttpData<*>>、
            val builder = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(json.asConverterFactory(contentType.toMediaType()))
                .addCallAdapterFactory(DeferredApiResultCallAdapterFactory())
//                .addCallAdapterFactory(DeferredCallAdapterFactory())
//                .addCallAdapterFactory(FlowCallAdapterFactory.create())
                .addConverterFactory(NullOnEmptyConverterFactory())
                .addCallAdapterFactory(ApiResultCallAdapterFactory())
                .client(okHttpClient())
            builder.block()
            mRetrofit = builder.build()
        }
        return this
    }

    /**
     * 提供默认的okHttpClient
     */
    private fun okHttpClient(): OkHttpClient {
        return mOkHttpClient ?: let {
            it.configOkHttpClient()
            mOkHttpClient!!
        }
    }

    fun <T> createApi(clazz: Class<T>): T = mRetrofit!!.create(clazz)

    companion object {

        @Volatile
        private var singleInstance: RetrofitHolder? = null

        /**
         * @param baseUrl baseUrl , suggest end with"/"
         * @param json json配置
         * @param useSingleInstance 是否使用单例模式。
         *    如果已经使用单例创建过，则之后即使使用非单例模式，假如有创建过的单例，会返回这个单例。
         */
        fun create(
            baseUrl: String,
            json: Json = Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            },
            useSingleInstance: Boolean = false,
        ): RetrofitHolder {
            if (useSingleInstance) {
                return singleInstance ?: synchronized(this) {
                    singleInstance ?: RetrofitHolder(baseUrl, json).also { singleInstance = it }
                }
            } else {
                return RetrofitHolder(baseUrl, json)
            }
        }

        fun getInstance(): RetrofitHolder? {
            return singleInstance
        }
    }
}