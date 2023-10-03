package com.kiylx.weather.repo.bean

import com.kiylx.libx.http.kotlin.basic2.Resource2
import kotlinx.serialization.Serializable

/**
 * 只返回详情成功且业务code为200的数据，其余返回null
 */
fun <T : BaseResponse> Resource2<T>.successData(): T? {
    return if (this is Resource2.Success) {
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
    abstract val code: String
    abstract val refer: Refer
    abstract val data: Any?
}

