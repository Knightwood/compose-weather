package com.kiylx.weather.repo.bean


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DailyEntity(
    @SerialName("code")
    override var code: String = "",
    @SerialName("fxLink")
    var fxLink: String = "",
    @SerialName("now")
    override var data: Now = Now(),
    @SerialName("refer")
    override var refer: Refer = Refer(),
    @SerialName("updateTime")
    var updateTime: String = ""
) : BaseResponse()

@Serializable
data class Now(
    @SerialName("cloud")
    var cloud: String = "",
    @SerialName("dew")
    var dew: String = "",
    @SerialName("feelsLike")
    var feelsLike: String = "",
    @SerialName("humidity")
    var humidity: String = "",
    @SerialName("icon")
    var icon: String = "",
    @SerialName("obsTime")
    var obsTime: String = "",
    @SerialName("precip")
    var precip: String = "",
    @SerialName("pressure")
    var pressure: String = "",
    @SerialName("temp")
    var temp: String = "",
    @SerialName("text")
    var text: String = "",
    @SerialName("vis")
    var vis: String = "",
    @SerialName("wind360")
    var wind360: String = "",
    @SerialName("windDir")
    var windDir: String = "",
    @SerialName("windScale")
    var windScale: String = "",
    @SerialName("windSpeed")
    var windSpeed: String = ""
)
