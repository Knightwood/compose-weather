package com.kiylx.libx.http.okhttp3.call_adapter

import com.kiylx.libx.http.request.RequestHandler
import com.kiylx.libx.http.response.ApiResult
import retrofit2.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType

/**
 * 声明：HttpData<*>为后端服务返回的数据结构
 *
 * 支持以下返回类型
 *  ApiResult<HttpData<SomeBean>>
 * 如果ApiResult的泛型是Call，而不是可序列化的数据类，将直接抛出异常。
 *
 * 与[DeferredApiResultCallAdapterFactory.DeferredApiResultCallAdapter]
 * 的区别在于DeferredApiResultCallAdapter使用了协程，
 * 而[ApiResultCallAdapterFactory.ApiResultCallAdapter]则是同步的。
 *
 * 使用：
 *
 * ```
 * Retrofit.Builder()
 *    .addCallAdapterFactory(ApiResultCallAdapterFactory())
 *    .build()
 * ```
 *
 * api：
 *
 * ```
 *
 * @POST("login")
 * fun login(
 *     @Body data: LoginFormData
 * ): ApiResult<HttpData<SomeBean>>
 * ```
 *
 * repo：
 *
 * ```
 * suspend fun login(loginFormData: LoginFormData): ApiResult<HttpData<SomeBean>> {
 *     return withContext(Dispatchers.IO){
 *         mainApi.login(loginFormData)
 *     }
 * }
 * ```
 */
class ApiResultCallAdapterFactory : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != ApiResult::class.java) {//ApiResult<*>
            return null
        }
        require(returnType is ParameterizedType) { "Call return type must be parameterized as Call<Foo> or Call<? extends Foo>" }
        val responseType = getParameterUpperBound(0, returnType)//获取returnType的第一层的泛型，HttpData<*>
        val responseClazz = getRawType(responseType)//解析得到 HttpData<SomeBean> 或者其他的可序列化的结构
        //我们只关心 ApiResult，至于他的泛型（后台返回数据解析的结构），我们不关心。
        //所以，如果返回类型为ApiResult<Call>就不管了
        //如果ApiResult的泛型不是Call，就执行请求，并返回结果。
        if (responseClazz == Call::class.java) {
            throw IllegalArgumentException("不支持 ApiResult<Call> 作为返回值")
        } else {
            require(responseType is ParameterizedType) { "ApiResult must be parameterized as ApiResult<Foo> or ApiResult<out Foo>" }
            return ApiResultCallAdapter<Any>(responseType) // HttpData<*>
        }
    }

    /**
     * 解析ApiResult<HttpData<SomeBean>> 格式
     *
     * @param T
     * @property responseType
     */
    class ApiResultCallAdapter<T : Any>(
        private val responseType: Type
    ) : CallAdapter<T, Any> {
        override fun responseType(): Type {
            return responseType
        }

        override fun adapt(call: Call<T>): Any {
            val deferred: ApiResult<T> = RequestHandler.handleInternal(call)
            return deferred
        }

    }
}