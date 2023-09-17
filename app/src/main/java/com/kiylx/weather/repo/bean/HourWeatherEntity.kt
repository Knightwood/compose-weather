package com.kiylx.weather.repo.bean


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HourWeatherEntity(
    @SerialName("code")
    override val code: String = "",
    @SerialName("fxLink")
    val fxLink: String = "",
    @SerialName("hourly")
    override val data: List<Hourly> = listOf(Hourly()),
    @SerialName("refer")
    override val refer: Refer = Refer(),
    @SerialName("updateTime")
    val updateTime: String = ""
) : BaseResponse() {
    @Serializable
    data class Hourly(
        @SerialName("cloud")
        val cloud: String = "",
        @SerialName("dew")
        val dew: String = "",
        @SerialName("fxTime")
        val fxTime: String = "2021-02-17T14:00+08:00",
        @SerialName("humidity")
        val humidity: String = "",
        @SerialName("icon")
        val icon: String = "999",
        @SerialName("pop")
        val pop: String = "",
        @SerialName("precip")
        val precip: String = "",
        @SerialName("pressure")
        val pressure: String = "",
        @SerialName("temp")
        val temp: String = "0",
        @SerialName("text")
        val text: String = "",
        @SerialName("wind360")
        val wind360: String = "",
        @SerialName("windDir")
        val windDir: String = "",
        @SerialName("windScale")
        val windScale: String = "",
        @SerialName("windSpeed")
        val windSpeed: String = ""
    )

}