package com.kiylx.weather.http

import com.kiylx.libx.http.kotlin.basic3.UiState
import com.kiylx.libx.http.kotlin.basic3.flow.DataUiState
import com.kiylx.libx.http.kotlin.common.RawResponse
import com.kiylx.weather.repo.bean.BaseResponse

suspend inline fun <reified T : BaseResponse> stateSendRequest(
    state: DataUiState<T>,
    action: () -> RawResponse<T>
) {
    state.setUiState(UiState.Loading)
    when (val response = action()) {
        is RawResponse.Error -> {
            state.setUiState(UiState.RequestErr(response))
        }

        is RawResponse.Success -> {
            if (response.responseData?.code == "200") {
                state.setDataOrState(
                    response.responseData,
                    UiState.Success(response.responseData)
                )
            } else {
                state.setUiState(
                    UiState.OtherErr(
                        response.responseData?.code?.toInt(),
                    )
                )
            }
        }
    }
}

suspend inline fun <reified T : BaseResponse> DataUiState<T>.sendRequest(action: () -> RawResponse<T>) {
    stateSendRequest(this, action)
}