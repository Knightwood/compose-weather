package com.kiylx.libx.http.kotlin.common

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.IOException

class RequestHandler {
    companion object{
        @PublishedApi
        internal suspend inline fun <reified T : Any, reified E : BaseErrorHandler>  handle(
            action: Call<T>,
            errorHandler: E?,
        ): RawResponse<T> {
            val rawResponse = handle {
                action.execute()
            }
            when(rawResponse){
                is RawResponse.Error -> {
                    errorHandler?.let {
                        when (rawResponse.errorMsg.errorType) {
                            ErrorType.DATE_FORMAT_ERROR,
                            ErrorType.NETWORK_ERROR -> it.ExceptionErr(rawResponse)
                            ErrorType.SERVICE_ERROR -> it.FailedErr(rawResponse)
                        }
                    }
                }
                is RawResponse.Success -> {
                    errorHandler?.OnSuccess(rawResponse)
                }
            }
            return rawResponse
        }

        /**
         * 执行网络请求，返回包裹了 成功、失败、异常 的原始值[RawResponse]
         */
        @PublishedApi
        internal suspend fun <T : Any> handle(
            action: () -> Response<T>,
        ): RawResponse<T> {
            var result: RawResponse<T>
            withContext(Dispatchers.IO) {
                try {
                    val response = action()
                    result = if (!response.isSuccessful) {
                        //这表示请求结果非200,服务器异常,将错误信息和响应码发送出去

                        RawResponse.Error(
                            ErrorResponse(
                                ErrorType.SERVICE_ERROR,
                                response.code(),
                                response.message()
                            )
                        )
                    } else {
                        //200
                        RawResponse.Success(response.body())
                    }

                } catch (e: IOException) {
                    //如果有IO异常,那说明是网络有问题,直接将错误信息的值发送出去
                    Log.e("tty2-handleRequest", "handleRequest:error ", e)
                    val err = ErrorResponse(ErrorType.NETWORK_ERROR, null, null)
                    result = RawResponse.Error(err, e)
                } catch (e:kotlinx.serialization.SerializationException){
                    Log.e("tty2-handleRequest", "handleRequest:类型转换错误 ", e)
                    val err = ErrorResponse(ErrorType.DATE_FORMAT_ERROR, null, null)
                    result = RawResponse.Error(err, e)
                }
            }
            return result
        }
    }
}
/**
 * 常用的mediaType 字符串
 */
class MediaTypeStr {
    companion object {
        const val stream = "application/octet-stream"
        const val form_data: String = "multipart/form-data"
        const val image: String = "image/*"
        const val text: String = "text/*"
        const val application_json :String ="application/json"
    }
}

/**
 * 将文件打包成MultipartBody.Part
 * @param mediaType 文件的媒体类型
 * @param name 参数名称
 */
fun File.packageToPart(
    mediaType: String,
    name: String,
    fileName: String = this.name,
): MultipartBody.Part {
    return MultipartBody.Part.createFormData(name,
        filename = fileName,
        this.asRequestBody(mediaType.toMediaTypeOrNull()))
}