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
    //排序
    var sortIndex:Int=0
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

    override fun hashCode(): Int {
        var result = adm1.hashCode()
        result = 31 * result + adm2.hashCode()
        result = 31 * result + country.hashCode()
        result = 31 * result + fxLink.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + isDst.hashCode()
        result = 31 * result + lat.hashCode()
        result = 31 * result + lon.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + rank.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + tz.hashCode()
        result = 31 * result + utcOffset.hashCode()
        result = 31 * result + default.hashCode()
        return result
    }

    companion object {
        fun Location.toLatLonStr(): String =
            String.format("%.2f", lon) + "," + String.format("%.2f", lat)

    }
}

