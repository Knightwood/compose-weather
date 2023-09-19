package com.kiylx.weather.repo.bean


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WarningEntity(
    @SerialName("code")
    override val code: String = "",
    @SerialName("fxLink")
    val fxLink: String = "",
    @SerialName("refer")
    override val refer: Refer = Refer(),
    @SerialName("updateTime")
    val updateTime: String = "",
    @SerialName("warning")
    override val data: List<Warning> = listOf()
) : BaseResponse() {

    @Serializable
    data class Warning(
        @SerialName("certainty")
        val certainty: String = "",
        @SerialName("endTime")
        val endTime: String = "",
        @SerialName("id")
        val id: String = "",
        @SerialName("level")
        val level: String = "",
        @SerialName("pubTime")
        val pubTime: String = "",
        @SerialName("related")
        val related: String = "",
        @SerialName("sender")
        val sender: String = "",
        @SerialName("severity")
        val severity: String = "",
        @SerialName("severityColor")
        val severityColor: String = "",
        @SerialName("startTime")
        val startTime: String = "",
        @SerialName("status")
        val status: String = "",
        @SerialName("text")
        val text: String = "",
        @SerialName("title")
        val title: String = "",
        @SerialName("type")
        val type: String = "",
        @SerialName("typeName")
        val typeName: String = "",
        @SerialName("urgency")
        val urgency: String = ""
    )
}