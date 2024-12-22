package com.kiylx.weather.repo.api

import com.kiylx.weather.http.CustomHeader
import com.kiylx.weather.repo.bean.LocationListEntity
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface GeoApi {
    /**
     * 城市搜索
     * @param location 需要查询地区的名称，支持文字、以英文逗号分隔的经度,纬度坐标（十进制，最多支持小数点后两位）、LocationID或Adcode（仅限中国城市）。
     *                 例如 location=北京 或 location=116.41,39.92
     * @param adm 城市的上级行政区划，可设定只在某个行政区划范围内进行搜索，
     *            用于排除重名城市或对结果进行过滤。例如 adm=beijing
     * @param range 搜索范围，可设定只在某个国家或地区范围内进行搜索，
     *              国家和地区名称需使用ISO 3166 所定义的国家代码。
     *              如果不设置此参数，搜索范围将在所有城市。例如 range=cn
     * @param number 返回结果的数量，取值范围1-20，默认返回10个结果
     * @param lang 多语言设置
     */
    @GET("v2/city/lookup")
    fun getCity(
        @Query("location") location: String,
        @Query("adm") adm: String?,
        @Query("range") range: String?,
        @Query("number") number: String?,
        @Query("lang") lang: String?,
        @Header(CustomHeader.cacheTime) cacheTime: Long?
    ):Deferred<LocationListEntity?>
}