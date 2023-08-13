package com.kiylx.libx.http.kotlin.basic2

import com.kiylx.libx.http.kotlin.common.RawResponse

sealed class Resource2<out T> {
    /**
     * 网络请求成功,服务器返回了结果
     */
    data class Success<out T>(val responseData: T? = null) : Resource2<T>()

    /**
     * 加载数据中
     */
    data class Loading<out T>(val data: T? = null) : Resource2<Nothing>()

    /**
     * 空白加载，或是初始值
     */
    object EmptyLoading : Resource2<Nothing>()

    /**
     * 其他错误，手动生成
     */
    data class LocalFailed(val data: LocalError) : Resource2<Nothing>()

    /**
     * 网络请求失败
     */
    data class RequestError(
        val err: RawResponse.Error,
    ) : Resource2<Nothing>()

    companion object {
        //快捷方法
        fun loading() = Loading<Nothing>()
        fun localErr(localError: LocalError) = LocalFailed(localError)
    }
}