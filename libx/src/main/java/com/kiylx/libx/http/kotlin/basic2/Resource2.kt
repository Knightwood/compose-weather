package com.kiylx.libx.http.kotlin.basic2

import com.kiylx.libx.http.kotlin.common.RawResponse

sealed class Resource2<out T> {

    /**
     * 请求成功，且服务器响应返回200。
     */
    data class Success<out T>(
        val responseData: T?,
    ) : Resource2<T>()

    /**
     * 网络请求失败，或是服务器响应非200
     */
    data class Error(
        val errorMsg: RawResponse.Error,
    ) : Resource2<Nothing>()

    /**
     * 加载数据中
     */
    data class Loading(val data: Any? = null) : Resource2<Nothing>()

    /**
     * 空白加载，或是初始值
     */
    data object EmptyLoading : Resource2<Nothing>()

    /**
     * 其他错误，手动生成
     */
    data class OtherError(
        val code: Int = defaultErrorCode,
        val msg: String = defaultErrorMsg
    ) : Resource2<Nothing>()

    companion object {
        const val defaultErrorCode = -1
        const val defaultErrorMsg = ""
    }
}