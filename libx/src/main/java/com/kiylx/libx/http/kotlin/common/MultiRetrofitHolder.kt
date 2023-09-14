package com.kiylx.libx.http.kotlin.common

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * 单例，且是懒加载。
 * 创建接口实例，存储不同baseurl下的retrofitholder实例
 */
object MultiRetrofitHolder {
    private var retrofitMap: HashMap<String, Retrofit2Holder> = hashMapOf()
    private val lock = ReentrantLock()

    fun <T : Retrofit2Holder> addRetrofit(baseUrl: String, retrofit2Holder: T) {
        lock.withLock {
            retrofitMap.putIfAbsent(baseUrl, retrofit2Holder)
        }
    }

    fun get(baseUrl: String): Retrofit2Holder? {
        return retrofitMap[baseUrl]
    }

    /**
     * 传入apiService接口，得到实例，如果baseurl不存在，返回null
     */
    fun <T> createServer(baseUrl: String, clazz: Class<T>): T? {
        return retrofitMap[baseUrl]?.create(clazz)
    }

}

/**
 * 持有okhttpclient和retrofit
 * 调用[config]方法，以实现自定义
 * @param baseUrl baseurl
 * ```
 * prettyPrint = true //json格式化
 * isLenient = true //宽松解析，json格式异常也可解析，如：{name:"小红",age:"18"} + Person(val name:String,val age:Int) ->Person("小红",18)
 * ignoreUnknownKeys = true //忽略未知键，如{"name":"小红","age":"18"} ->Person(val name:String)
 * coerceInputValues =  true //强制输入值，如果json属性与对象格式不符，则使用对象默认值，如：{"name":"小红","age":null} + Person(val name:String = "小绿"，val age:Int = 18) ->Person("小红",18)
 * encodeDefaults =  true //编码默认值,默认情况下，默认值的属性不会参与序列化，通过设置encodeDefaults = true,可让默认属性参与序列化(可参考上述例子)
 * explicitNulls =  true //序列化时是否忽略null
 * allowStructuredMapKeys =  true //允许结构化映射(map的key可以使用对象)
 * allowSpecialFloatingPointValues =  true //特殊浮点值：允许Double为NaN或无穷大
```
 */
@OptIn(ExperimentalSerializationApi::class)
open class Retrofit2Holder(
    var baseUrl: String,
) {
    var json: Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }
    var contentType = "application/json".toMediaType()
    var mOkHttpClient: OkHttpClient? = OkhttpClientProvider.okHttpClient
    var mRetrofit: Retrofit

    init {
        val builder = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(json.asConverterFactory(contentType))
            .client(mOkHttpClient!!)
        mRetrofit = builder.build()
    }

    /**
     * 调用此方法实现自定义内部配置
     */
    fun config(block: Retrofit2Holder.() -> Unit) {
        this.block()
    }

    /**
     * 传入apiService接口，得到实例
     */
    fun <T> create(clazz: Class<T>): T = mRetrofit.create(clazz)
}

/**
 * 单例，提供okHttpClient实例
 */
object OkhttpClientProvider {
    //每次获取okHttpClient是否都调用一次build()
    var reCreate = false
    lateinit var builder: OkHttpClient.Builder
    var okHttpClient: OkHttpClient? = null
        get() {
            if (field == null || reCreate) {
                field = builder.build()
            }
            return field
        }

    /**
     * 需要在application种调用，去初始化OkhttpClient
     */
    fun configOkHttpClient(block: OkHttpClient.Builder.() -> Unit) {
        if (this::builder.isInitialized) {
            if (reCreate) {
                builder.block()
            } else {
                return
            }
        } else {
            builder = OkHttpClient.Builder()
            builder.block()
        }

    }
}