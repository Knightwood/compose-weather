package com.kiylx.libx.http.okhttp3.call_adapter

import com.kiylx.libx.http.response.ApiResult
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import retrofit2.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType

/**
 * 声明：HttpData<*>为后端服务返回的数据结构
 * 支持以下返回类型
 * Deferred<ApiResult<HttpData<SomeBean>>>
 * 如果Deferred的泛型不是ApiResult，将使用[DeferredCallAdapterFactory.DeferredCallAdapter]
 * 按照Deferred<HttpData<SomeBean>> 格式解析
 *
 * 使用：
 *
 * ```
 * Retrofit.Builder()
 *    .addCallAdapterFactory(DeferredCallAdapterFactory())
 *    .build()
 * ```
 *
 * api：
 *
 * ```
 * @GET("user/findToken")
 * fun findToken(
 *     @Query("roleId") roleId: Int?,
 *     @Query("deptId") deptId: Int?,
 *     @Header("token") token: String = TokenUtils.accessToken
 * ): Deferred<ApiResult<HttpData<SomeBean>>>
 *
 * ```
 *
 * repo：
 *
 * ```
 * suspend fun findToken(roleId: Int? = null, deptId: Int? = null): ApiResult<HttpData<SomeBean>> {
 *     return mainApi.findToken(roleId, deptId).await()
 * }
 * ```
 */
class DeferredApiResultCallAdapterFactory : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != Deferred::class.java) {//Deferred<*>
            return null
        }
        require(returnType is ParameterizedType) { "Call return type must be parameterized as Call<Foo> or Call<? extends Foo>" }
        val responseType = getParameterUpperBound(0, returnType)//获取returnType的第一层的泛型，即ApiResult<*>
        val responseClazz = getRawType(responseType)//解析得到 ApiResult<HttpData<SomeBean>>
        if (responseClazz == ApiResult::class.java) {
            require(responseType is ParameterizedType) { "ApiResult must be parameterized as ApiResult<Foo> or ApiResult<out Foo>" }
            return DeferredApiResultCallAdapter<Any>(
                getParameterUpperBound(
                    0,
                    responseType
                )
            )//这里获取了第二层泛型 HttpData<SomeBean>
        } else {
            //如果不是Deferred<ApiResult<HttpData<SomeBean>>>，则当作Deferred<HttpData<SomeBean>>解析
            return DeferredCallAdapterFactory.DeferredCallAdapter<Any>(responseType)
        }
    }

    /**
     * 解析Deferred<ApiResult<HttpData<SomeBean>>> 格式
     *
     * @param T
     * @property responseType
     */
    class DeferredApiResultCallAdapter<T>(
        private val responseType: Type
    ) : CallAdapter<T, Any> {
        override fun responseType(): Type {
            return responseType
        }

        override fun adapt(call: Call<T>): Any {
            val deferred = CompletableDeferred<ApiResult<T>>()

            deferred.invokeOnCompletion {
                if (deferred.isCancelled) {
                    call.cancel()
                }
            }

            call.enqueue(object : Callback<T> {
                override fun onFailure(call: Call<T>, t: Throwable) {
                    deferred.complete(ApiResult.ExceptionErr(t))
                }

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    if (response.isSuccessful && response.body() != null) {
                        deferred.complete(ApiResult.Success(response))
                    } else {
                        deferred.complete(ApiResult.ExceptionErr(HttpException(response)))
                    }
                }
            })

            return deferred
        }
    }


    companion object {
        /**
         * 获取第几层泛型的type，其实就是[retrofit2.CallAdapter.Factory.getParameterUpperBound]
         *
         * @param index
         * @param type
         * @return
         */
        fun getParameterUpperBound(index: Int, type: ParameterizedType): Type {
            val types = type.actualTypeArguments
            require(!(index < 0 || index >= types.size)) { "Index $index not in range [0,${types.size}) for $type" }
            val paramType = types[index]
            return if (paramType is WildcardType) {
                paramType.upperBounds[0]
            } else paramType
        }
    }
}