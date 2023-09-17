package com.kiylx.weather.repo.bean


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocationListEntity(
    override val code: String,
    override val refer: Refer = Refer(),
    @SerialName("location")
    override val data: List<Location> = listOf(),
) : BaseResponse()

@Serializable
data class Location(
    @SerialName("adm1")
    val adm1: String = "山东省",
    @SerialName("adm2")
    val adm2: String = "泰安",
    @SerialName("country")
    val country: String = "中国",
    @SerialName("fxLink")
    val fxLink: String = "",
    @SerialName("id")
    val id: String = "",
    @SerialName("isDst")
    val isDst: String = "",
    @SerialName("lat")
    val lat: String = "36.18931",
    @SerialName("lon")
    val lon: String = "117.12998",
    @SerialName("name")
    val name: String = "",
    @SerialName("rank")
    val rank: String = "",
    @SerialName("type")
    val type: String = "",
    @SerialName("tz")
    val tz: String = "",
    @SerialName("utcOffset")
    val utcOffset: String = "",

    //是否是默认位置
    var default: Boolean = false
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
        fun Location.toLatLonStr(): String {
           return String.format("%.2f", lon.toDouble()) + "," + String.format("%.2f", lat.toDouble())
        }

    }
}

