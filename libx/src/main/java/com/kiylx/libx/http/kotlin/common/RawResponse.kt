package com.kiylx.libx.http.kotlin.common

import java.lang.Exception
import com.kiylx.libx.http.kotlin.basic.Resource

/**
 * 执行网络请求时，包裹成功或失败，异常的原始值
 * 解析call得到response,生成response时成功或捕获到异常,由此产生原始的解析结果
 *
 * 也可以将[RawResponse]进一步处理处理，将返回给网络请求者[Resource]等额外的请求结果
 */
sealed class RawResponse<out T> {


    /**
     * 请求成功，且服务器响应返回200。
     */
    data class Success<out T>(
        val responseData: T?,
    ) : RawResponse<T>()

    /**
     * 网络请求失败，或是服务器响应非200
     */
    data class Error(
        val errorMsg: ErrorResponse,
        val exception: Exception? = null,
    ) : RawResponse<Nothing>()

}

/**
 * 网络请求出错的响应
 */
data class ErrorResponse(
    val errorType: ErrorType,//错误类型
    //val errorTag:String,//错误tag,用于区别哪个请求出错
    val errorCode: Int?,//错误代码
    val message: String?,//错误信息
)

enum class ErrorType {
    NETWORK_ERROR,//网络原因失败
    SERVICE_ERROR,//请求失败服务器响应非200
    DATE_FORMAT_ERROR//请求返回数据反序列化失败
}

typealias Resource2<T> =RawResponse<T>

/**
 * 解析错误原因
 */
inline infix fun RawResponse.Error.parseError(block: (s: String) -> Unit) {
    when (errorMsg.errorType) {
        ErrorType.NETWORK_ERROR -> {
            exception?.let { exception ->
                when (exception) {
                    is java.net.SocketTimeoutException -> {
                        block("网络连接超时，请稍后再试")
                    }
                    is java.net.ConnectException -> {
                        block("网络连接失败，请稍后再试")
                    }
                    is okhttp3.internal.http2.ConnectionShutdownException -> {
                        block("连接已关闭，请稍后再试")
                    }
                    else -> {
                        block("网络开小差了，请稍后再试")
                    }
                }
            }
        }
        ErrorType.SERVICE_ERROR -> {
            block(errorMsg.message.toString())
        }
        ErrorType.DATE_FORMAT_ERROR->{
            block("反序列化失败")
        }
    }
}
