package com.kiylx.weather.repo.bean


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DayWeather(
    @SerialName("code")
    override var code: String = "",
    @SerialName("refer")
    override var refer: Refer = Refer(),
    @SerialName("daily")
    override val data: List<OneDayWeather> = listOf(OneDayWeather()),
    @SerialName("fxLink")
    val fxLink: String = "",
    @SerialName("updateTime")
    val updateTime: String = ""
) : BaseResponse()

@Serializable
data class OneDayWeather(
    @SerialName("cloud")
    val cloud: String = "",
    @SerialName("fxDate")
    val fxDate: String = "1972-01-01",
    @SerialName("humidity")
    val humidity: String = "",
    @SerialName("iconDay")
    val iconDay: String = "999",
    @SerialName("iconNight")
    val iconNight: String = "999",
    @SerialName("moonPhase")
    val moonPhase: String = "",
    @SerialName("moonPhaseIcon")
    val moonPhaseIcon: String = "999",
    @SerialName("moonrise")
    val moonrise: String = "",
    @SerialName("moonset")
    val moonset: String = "",
    @SerialName("precip")
    val precip: String = "",
    @SerialName("pressure")
    val pressure: String = "",
    @SerialName("sunrise")
    val sunrise: String = "",
    @SerialName("sunset")
    val sunset: String = "",
    @SerialName("tempMax")
    val tempMax: String = "0",
    @SerialName("tempMin")
    val tempMin: String = "0",
    @SerialName("textDay")
    val textDay: String = "",
    @SerialName("textNight")
    val textNight: String = "",
    @SerialName("uvIndex")
    val uvIndex: String = "0",
    @SerialName("vis")
    val vis: String = "0",
    @SerialName("wind360Day")
    val wind360Day: String = "",
    @SerialName("wind360Night")
    val wind360Night: String = "",
    @SerialName("windDirDay")
    val windDirDay: String = "",
    @SerialName("windDirNight")
    val windDirNight: String = "",
    @SerialName("windScaleDay")
    val windScaleDay: String = "",
    @SerialName("windScaleNight")
    val windScaleNight: String = "",
    @SerialName("windSpeedDay")
    val windSpeedDay: String = "",
    @SerialName("windSpeedNight")
    val windSpeedNight: String = ""
)