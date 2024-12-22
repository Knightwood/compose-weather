package com.kiylx.weather.repo.bean


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 每日空气质量
 */
@Serializable
data class DayAirEntity(
    @SerialName("code")
    override val code: String = "",
    @SerialName("daily")
    override val data: List<Daily> = listOf(Daily()),
    @SerialName("fxLink")
    val fxLink: String = "",
    @SerialName("refer")
    override val refer: Refer = Refer(),
    @SerialName("updateTime")
    val updateTime: String = ""
) : HttpData() {
    @Serializable
    data class Daily(
        @SerialName("aqi")
        val aqi: String = "",
        @SerialName("category")
        val category: String = "",
        @SerialName("fxDate")
        val fxDate: String = "",
        @SerialName("level")
        val level: String = "",
        @SerialName("primary")
        val primary: String = ""
    )

}