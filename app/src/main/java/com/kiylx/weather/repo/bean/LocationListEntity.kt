package com.kiylx.weather.repo.bean


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocationListEntity(
    override var code: String,
    override var refer: Refer = Refer(),
    @SerialName("location")
    override val data: List<Location> = listOf(),
) : BaseResponse()

@Serializable
data class Location(
    @SerialName("adm1")
    var adm1: String = "",
    @SerialName("adm2")
    var adm2: String = "",
    @SerialName("country")
    var country: String = "",
    @SerialName("fxLink")
    var fxLink: String = "",
    @SerialName("id")
    var id: String = "",
    @SerialName("isDst")
    var isDst: String = "",
    @SerialName("lat")
    var lat: String = "",
    @SerialName("lon")
    var lon: String = "",
    @SerialName("name")
    var name: String = "",
    @SerialName("rank")
    var rank: String = "",
    @SerialName("type")
    var type: String = "",
    @SerialName("tz")
    var tz: String = "",
    @SerialName("utcOffset")
    var utcOffset: String = "",

    //是否是默认位置
    var default: Boolean = false,
) {

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        return if (other !is Location) {
            false
        } else {
            //如果这两个默认位置，只比较位置
            //在经纬度相同的时候，这是同一个位置信息
            if (other.default == default) {
                (other.lat == lat) && (other.lon == lon)
            } else {
                (other.lat == lat) && (other.lon == lon) && (other.id == id)
            }
        }
    }
}

