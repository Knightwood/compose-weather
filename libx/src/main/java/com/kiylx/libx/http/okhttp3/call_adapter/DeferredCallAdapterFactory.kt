package com.kiylx.libx.http.okhttp3.call_adapter


import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import retrofit2.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * 声明：HttpData<*>为后端服务返回的数据结构
 *
 * 支持以下返回类型 Deferred<HttpData<SomeBean>>
 * 如果Deferred的泛型是Call，将使用[DeferredCallAdapterFactory.DeferredCallThrowAdapter]
 * 按照Deferred<Call> 格式解析，此时，如果执行call过程中发生异常，将抛出异常。
 *
 * 与[DeferredApiResultCallAdapterFactory] 不同的是：
 * 这个直接返回服务器的业务数据，而不管请求是否发生了异常或者http code非200的情况 使用：
 *
 * ```
 * Retrofit.Builder()
 *    .addCallAdapterFactory(DeferredCallAdapterFactory(HttpData.empty()))
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
 * ): Deferred<HttpData<SomeBean>?>
 *
 * ```
 *
 * repo：
 *
 * ```
 * suspend fun findToken(roleId: Int? = null, deptId: Int? = null): HttpData<SomeBean>? {
 *     return mainApi.findToken(roleId, deptId).await()
 * }
 *
 * //使用：
 * val result =findToken(...)
 * //需要注意的是：请求出现异常、http status code 非2XX都会返回null
 * //而正常的到请求结果后，不要忘记自己判断一下业务code
 * result.takeIf { it!=null && it.code==200 }?.let {
 *      //这里得到了结果（也就是说请求HttpCode:2XX且业务code:200）
 * }
 *
 * //如果使用了HttpData类，可以简化为：
 * result.takeIf { it.isOK() }?.let {}
 * ```
 *
 * @property canThrowErr 解析请求时，发生异常或http code非2XX，是否对Deferred抛出异常
 */
class DeferredCallAdapterFactory(private val canThrowErr: Boolean = false) : CallAdapter.Factory() {

    override fun get(
        returnType: Type, /* Deferred<HttpData<SomeBean>> */
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != Deferred::class.java) {//Deferred<*>
            return null
        }
        require(returnType is ParameterizedType) { "Call return type must be parameterized as Call<Foo> or Call<? extends Foo>" }
        val responseType = getParameterUpperBound(0, returnType)//获取returnType (即Deferred<HttpData<SomeBean>> )的第一层的泛型，即HttpData<*>
        if (canThrowErr) {
            return DeferredCallThrowAdapter<Any>(responseType)//这里请求得到第二层泛型 HttpData<SomeBean>
        }
        return DeferredCallAdapter<Any>(responseType)//这里请求得到第二层泛型 HttpData<SomeBean>
    }

    /**
     * 解析Deferred<HttpData<SomeBean>> 格式
     *
     * @param T 是HttpData<*>类型
     * @property responseType
     */
    class DeferredCallAdapter<T>(
        private val responseType: Type
    ) : CallAdapter<T, Any> {
        override fun responseType(): Type {
            return responseType
        }

        override fun adapt(call: Call<T>): Any {
            //如果请求失败、http code非200，则请求结果数据为null
            //而不论请求结果是不是null，我们都返回deferred,因此，实际类型是CompletableDeferred<T?>
            val deferred = CompletableDeferred<T?>()

            deferred.invokeOnCompletion {
                if (deferred.isCancelled) {
                    call.cancel()
                }
            }

            call.enqueue(object : Callback<T> {
                override fun onFailure(call: Call<T>, t: Throwable) {
                    deferred.complete(null)
                }

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    if (response.isSuccessful && response.body() != null) {
                        deferred.complete(response.body())
                    } else {
                        deferred.complete(null)
                    }
                }
            })

            return deferred
        }
    }

    /**
     * 实际上跟上面的[DeferredCallAdapter]解析格式一致。 不同之处在于：如果发生异常或是请求失败，
     * 将调用deferred.completeExceptionally()抛出异常，而不是返回null
     *
     * @param T
     * @property responseType
     */
    class DeferredCallThrowAdapter<T>(
        private val responseType: Type
    ) : CallAdapter<T, Any> {
        override fun responseType(): Type {
            return responseType
        }

        override fun adapt(call: Call<T>): Any {
            val deferred = CompletableDeferred<T>()

            deferred.invokeOnCompletion {
                if (deferred.isCancelled) {
                    call.cancel()
                }
            }

            call.enqueue(object : Callback<T> {
                override fun onFailure(call: Call<T>, t: Throwable) {
                    deferred.completeExceptionally(t)
                }

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    if (response.isSuccessful && response.body() != null) {
                        deferred.complete(response.body()!!)
                    } else {
                        deferred.completeExceptionally(HttpException(response))
                    }
                }
            })

            return deferred
        }
    }
}