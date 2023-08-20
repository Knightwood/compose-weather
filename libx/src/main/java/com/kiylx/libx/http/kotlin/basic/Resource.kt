package com.kiylx.libx.http.kotlin.basic

import com.kiylx.libx.http.kotlin.common.RawResponse

/**
 * 创建者 kiylx
 * 创建时间 2022/5/7 20:10
 * packageName：com.crystal.aplayer.module_base.base.http.okhttp
 * 描述：
 * HttpCode:200 ->success
 * HttpCode非200 ->Error
 */

class Resource<out T : Any>(
    val status: Status,//解析服务器返回结果和解析response后，综合得到的结果，可以根据它判断数据获取成功或失败
    val responseData: T? = null,//服务器返回的数据

    val rawError: RawResponse.Error? = null,//解析response得到的错误
    val localErr: LocalError? = null,
) {
    companion object {
        fun <T> success(responseData: T?): Resource<T & Any> {
            return Resource(status = Status.SUCCESS, responseData = responseData)
        }

        /**
         * @param error ResponseResult得到的消息，比如无网络链接,请求失败HTTPCODE非200
         */
        fun error(error: RawResponse.Error): Resource<Nothing> {
            return Resource(status = Status.REQUEST_ERROR, rawError = error)
        }

        /**
         * 本地问题的导致失败
         */
        fun failed(localErr: LocalError?): Resource<Nothing> {
            return Resource<Nothing>(status = Status.LOCAL_ERR, localErr = localErr)
        }

        fun <T> loading(data: T?): Resource<T & Any> {
            return Resource(status = Status.LOADING, responseData = data)
        }
    }
}