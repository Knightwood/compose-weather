package com.kiylx.libx.http.kotlin.basic3

import com.kiylx.libx.http.kotlin.common.RawResponse

sealed class UiState {

    data object INIT : UiState()

    /**
     * 成功，显示界面。也可以携带数据
     */
    data class Success<T>(val data: T? = null) : UiState()

    /**
     * 网络请求错误
     */
    data class RequestErr(
        val requestError: RawResponse.Error? = null
    ) : UiState()

    /**
     * 除了网络请求之外的其他错误
     */
    data class OtherErr(
        val code: Int? = null,
        val msg: String? = null,
        val throwable: Throwable? = null
    ) : UiState()

    data object Loading : UiState()

    data object Empty : UiState()

}
