package com.kiylx.weather.repo.bean


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 天气指数
 */
@Serializable
data class IndicesEntity(
    @SerialName("code")
    override val code: String = "",
    @SerialName("daily")
    override val data: List<Daily> = listOf(),
    @SerialName("fxLink")
    val fxLink: String = "",
    @SerialName("refer")
    override val refer: Refer = Refer(),
    @SerialName("updateTime")
    val updateTime: String = ""
) : BaseResponse(){
    @Serializable
    data class Daily(
        @SerialName("category")
        val category: String = "",
        @SerialName("date")
        val date: String = "",
        @SerialName("level")
        val level: String = "",
        @SerialName("name")
        val name: String = "",
        @SerialName("text")
        val text: String = "",
        @SerialName("type")
        val type: String = ""
    )

}