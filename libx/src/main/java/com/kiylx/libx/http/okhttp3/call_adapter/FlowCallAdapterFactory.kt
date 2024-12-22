package com.kiylx.libx.http.okhttp3.call_adapter

import kotlinx.coroutines.flow.Flow
import retrofit2.CallAdapter
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


/**
 * 自定义了一个CallAdapter，这样可以控制请求返回什么结果类型
 *
 * 请注意，对于[ApiResultCallAdapterFactory]，请求只要不报错就是success。
 * 但这个CallAdapter，请求不报异常，http code为2XXbody不为null，才算是成功。
 *
 * 使用：
 * ```
 * Retrofit.Builder()
 *    .addCallAdapterFactory(FlowCallAdapterFactory.create())
 *    .build()
 * ```
 *
 * api：
 * ```
 *     @POST("/c/c/img/portrait")
 *     fun imgPortrait(
 *         @Body body: RequestBody,
 *         @retrofit2.http.Header(Header.USER_AGENT) user_agent: String = "bdtb for Android 11.10.8.6",
 *     ): Flow<HttpData<SomeBean>> //这里不能写上 Flow<ApiResult<HttpData<SomeBean>>>
 * ```
 *
 */
class FlowCallAdapterFactory private constructor() : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != Flow::class.java) {
            return null
        }
        check(returnType is ParameterizedType) { "Flow return type must be parameterized as Flow<Foo> or Flow<out Foo>" }
        val responseType = getParameterUpperBound(0, returnType)
        val rawFlowType = getRawType(responseType)
        return if (rawFlowType == Response::class.java) {// 对于api返回类型为Flow<Response>
            check(responseType is ParameterizedType) { "Response must be parameterized as Response<Foo> or Response<out Foo>" }
            ResponseCallAdapter<Any>(
                getParameterUpperBound(
                    0,
                    responseType
                )
            )
        } else {// 对于api返回类型为 Flow<数据类>
            BodyCallAdapter<Any>(responseType)
        }
    }

    companion object {
        @JvmStatic
        fun create() = FlowCallAdapterFactory()
    }
}