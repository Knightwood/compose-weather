package com.kiylx.weather.repo.bean


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 实时空气质量
 */
@Serializable
data class DailyAirEntity(
    @SerialName("code")
    override val code: String = "",
    @SerialName("fxLink")
    val fxLink: String = "",
    @SerialName("now")
    override val data: Now = Now(),
    @SerialName("refer")
    override val refer: Refer = Refer(),
    @SerialName("station")
    val station: List<Station> = listOf(),
    @SerialName("updateTime")
    val updateTime: String = ""
) : BaseResponse() {
    @Serializable
    data class Now(
        @SerialName("aqi")
        val aqi: String = "",
        @SerialName("category")
        val category: String = "",
        @SerialName("co")
        val co: String = "",
        @SerialName("level")
        val level: String = "",
        @SerialName("no2")
        val no2: String = "",
        @SerialName("o3")
        val o3: String = "",
        @SerialName("pm10")
        val pm10: String = "",
        @SerialName("pm2p5")
        val pm2p5: String = "",
        @SerialName("primary")
        val primary: String = "",
        @SerialName("pubTime")
        val pubTime: String = "",
        @SerialName("so2")
        val so2: String = ""
    )

    @Serializable
    data class Station(
        @SerialName("aqi")
        val aqi: String = "",
        @SerialName("category")
        val category: String = "",
        @SerialName("co")
        val co: String = "",
        @SerialName("id")
        val id: String = "",
        @SerialName("level")
        val level: String = "",
        @SerialName("name")
        val name: String = "",
        @SerialName("no2")
        val no2: String = "",
        @SerialName("o3")
        val o3: String = "",
        @SerialName("pm10")
        val pm10: String = "",
        @SerialName("pm2p5")
        val pm2p5: String = "",
        @SerialName("primary")
        val primary: String = "",
        @SerialName("pubTime")
        val pubTime: String = "",
        @SerialName("so2")
        val so2: String = ""
    )
}