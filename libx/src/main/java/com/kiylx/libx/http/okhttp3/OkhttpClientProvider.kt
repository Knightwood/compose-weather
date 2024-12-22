package com.kiylx.libx.http.okhttp3

import okhttp3.OkHttpClient

/*
 * 持有okhttpclient和retrofit 调用[config]方法，以实现自定义
 *
 * @param baseUrl baseurl
 *
 * ```
 * prettyPrint = true //json格式化
 * isLenient = true //宽松解析，json格式异常也可解析，如：{name:"小红",age:"18"} + Person(val name:String,val age:Int) ->Person("小红",18)
 * ignoreUnknownKeys = true //忽略未知键，如{"name":"小红","age":"18"} ->Person(val name:String)
 * coerceInputValues =  true //强制输入值，如果json属性与对象格式不符，则使用对象默认值，如：{"name":"小红","age":null} + Person(val name:String = "小绿"，val age:Int = 18) ->Person("小红",18)
 * encodeDefaults =  true //编码默认值,默认情况下，默认值的属性不会参与序列化，通过设置encodeDefaults = true,可让默认属性参与序列化(可参考上述例子)
 * explicitNulls =  true //序列化时是否忽略null
 * allowStructuredMapKeys =  true //允许结构化映射(map的key可以使用对象)
 * allowSpecialFloatingPointValues =  true //特殊浮点值：允许Double为NaN或无穷大
 * ```
 */
/**
 * 单例，全局提供单个okHttpClient实例
 */
object OkhttpClientProvider {
    lateinit var okHttpClient: OkHttpClient
        private set

    /**
     * 需要在application种调用，去初始化OkhttpClient
     *
     * @param newBuilder
     *    1. 如果okHttpClient已经存在，true:使用已存在的okHttpClient创建新的builder并进行配置。false：直接返回，不进行配置。
     *    2. 如果okHttpClient不存在，则直接创建新的实例并进行配置，newBuilder参数无效
     */
    fun configOkHttpClient(
        newBuilder: Boolean = false,
        block: OkHttpClient.Builder.() -> Unit = {}
    ): OkHttpClient {
        val isInitialized = this::okHttpClient.isInitialized
        if (isInitialized && !newBuilder) return okHttpClient//true,false 直接返回存在的okHttpClient
        val builder = if (isInitialized) {
            //true,true 使用newBuilder
            okHttpClient.newBuilder()
        } else {
            //false,true
            //false,false
            //创建新的实例
            OkHttpClient.Builder()
        }
        builder.block()
        okHttpClient = builder.build()
        return okHttpClient
    }
}
