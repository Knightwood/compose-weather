package com.kiylx.weather.repo.bean

import kotlinx.serialization.Serializable

@Serializable
sealed class HttpData{
    abstract val code: String
    abstract val refer: Refer
    abstract val data: Any?
}

typealias BaseResponse = HttpData
