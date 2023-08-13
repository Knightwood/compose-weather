package com.kiylx.weather.repo.bean

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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