package com.kiylx.weather.repo.bean


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MinutelyPrecipitationEntity(
    @SerialName("code")
    override val code: String = "",
    @SerialName("fxLink")
    val fxLink: String = "",
    @SerialName("minutely")
    override val data: List<Minutely> = listOf(),
    @SerialName("refer")
    override val refer: Refer = Refer(),
    @SerialName("summary")
    val summary: String = "",
    @SerialName("updateTime")
    val updateTime: String = ""
) : BaseResponse() {
    @Serializable
    data class Minutely(
        @SerialName("fxTime")
        val fxTime: String = "",
        @SerialName("precip")
        val precip: String = "0",
        @SerialName("type")
        val type: String = ""
    )
}