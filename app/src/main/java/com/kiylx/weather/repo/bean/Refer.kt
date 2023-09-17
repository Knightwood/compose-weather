package com.kiylx.weather.repo.bean

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Refer(
    @SerialName("license")
    val license: List<String> = listOf(),
    @SerialName("sources")
    val sources: List<String> = listOf()
)