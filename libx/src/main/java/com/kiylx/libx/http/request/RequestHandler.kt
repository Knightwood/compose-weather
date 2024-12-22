package com.kiylx.libx.http.request

import android.util.Log
import com.kiylx.libx.http.response.ApiResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.HttpException
import java.io.IOException
import java.lang.RuntimeException

/**
 * 用于网络请求。
 *
 * 在api中定义：
 *
 * ```
 *     @GET("system/queryUser/{id}")
 *     fun query1(
 *         @Path("id") id: String,
 *     ): Call<HttpData<ExampleBean>>
 * ```
 *
 * 在repo中使用：
 *
 * ```
 *  suspend fun post1(id: String): ApiResult<HttpData<ExampleBean>> {
 *         return RequestHandler.handle(
 *             mainApi.query1(id)
 *         )
 *     }
 * ```
 */
object RequestHandler {

    suspend fun <T : Any> handle(
        action: Call<T>,
    ): ApiResult<T> {
        var result: ApiResult<T>
        withContext(Dispatchers.IO) {
            result = handleInternal(action)
        }
        return result
    }

    fun <T : Any> handleInternal(
        action: Call<T>,
    ): ApiResult<T> {
        var result: ApiResult<T>
        try {
            val response = action.execute()
            //2024-11-13 非2XX的结果，就应该是异常的。
            result = if (response.isSuccessful && response.body() != null) {
                ApiResult.Success(response)
            } else {
//                Log.e(TAG, "handleInternal: ${response.code()} ${response.message()}", )
                ApiResult.ExceptionErr(HttpException(response))
            }
        } catch (e: java.net.ConnectException) {
            //网络有问题,直接将错误信息的值发送出去
            Log.e("tty2-handleRequest", "ConnectException ", e)
            result = ApiResult.ExceptionErr(e, "网络连接异常")
        } catch (e: kotlinx.serialization.SerializationException) {
            Log.e("tty2-handleRequest", "类型转换错误 ", e)
            result = ApiResult.ExceptionErr(e, "反序列化失败: ${e.message}")
        } catch (e: IOException) {
            //如果有IO异常,那说明是网络有问题,直接将错误信息的值发送出去
            Log.e("tty2-handleRequest", "IOException ", e)
            result = ApiResult.ExceptionErr(e, "IO异常: ${e.message}")
        } catch (e: RuntimeException) {
            Log.e("tty2-handleRequest", "RuntimeException ", e)
            result = ApiResult.ExceptionErr(e, "运行异常: ${e.message}")
        }
        return result
    }

    const val TAG ="tty1-请求处理"
}