package com.kiylx.weather.repo.bean

import com.kiylx.libx.http.kotlin.common.RawResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 只返回详情成功且业务code为200的数据，其余返回null
 */
fun <T : BaseResponse> RawResponse<T>.successData(): T? {
    return if (this is RawResponse.Success) {
        val res = this.responseData
        if (res != null && res.code == "200")
            res
        else {
            null
        }
    } else {
        null
    }
}

@Serializable
sealed class BaseResponse {
    abstract var code: String
    abstract var refer: Refer
    abstract val data: Any?
}

@Serializable
data class Refer(
    @SerialName("license")
    var license: List<String> = listOf(),
    @SerialName("sources")
    var sources: List<String> = listOf()
)