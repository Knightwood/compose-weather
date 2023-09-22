package com.kiylx.weather.repo.api

import com.kiylx.weather.http.CustomHeader
import com.kiylx.weather.repo.bean.DailyAirEntity
import com.kiylx.weather.repo.bean.DailyEntity
import com.kiylx.weather.repo.bean.DayAirEntity
import com.kiylx.weather.repo.bean.DayWeather
import com.kiylx.weather.repo.bean.HourWeatherEntity
import com.kiylx.weather.repo.bean.IndicesEntity
import com.kiylx.weather.repo.bean.MinutelyPrecipitationEntity
import com.kiylx.weather.repo.bean.WarningEntity
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface Api {
    /**
     * @param unit 数据单位设置，可选值包括unit=m（公制单位，默认）和unit=i（英制单位)
     */
    @GET("v7/weather/now")
    fun getDaily(
        @Query("location") location: String,
        @Query("lang") lang: String?,
        @Query("unit") unit: String?,
        @Header(CustomHeader.cacheTime) cacheTime: Long?
    ): Call<DailyEntity>

    @GET("v7/grid-weather/now")
    fun getGridDaily(
        @Query("location") location: String,
        @Query("lang") lang: String?,
        @Query("unit") unit: String?,
        @Header(CustomHeader.cacheTime) cacheTime: Long?
    ): Call<DailyEntity>

    @GET("v7/warning/now")
    fun getWarningNow(
        @Query("location") location: String,
        @Query("lang") lang: String?,
        @Header(CustomHeader.cacheTime) cacheTime: Long?
    ): Call<WarningEntity>

    /**
     * 分钟级降水
     */
    @GET("v7/minutely/5m")
    fun getMinutelyPrecipitation(
        @Query("location") location: String,
        @Query("lang") lang: String?,
        @Header(CustomHeader.cacheTime) cacheTime: Long?
    ): Call<MinutelyPrecipitationEntity>

    @GET("v7/weather/24h")
    fun getHourWeather(
        @Query("location") location: String,
        @Query("lang") lang: String?,
        @Query("unit") unit: String?,
        @Header(CustomHeader.cacheTime) cacheTime: Long?
    ): Call<HourWeatherEntity>

    @GET("v7/grid-weather/24h")
    fun getGridHourWeather(
        @Query("location") location: String,
        @Query("lang") lang: String?,
        @Query("unit") unit: String?,
        @Header(CustomHeader.cacheTime) cacheTime: Long?
    ): Call<HourWeatherEntity>

    @GET("v7/weather/3d")
    fun getDayWeather3d(
        @Query("location") location: String,
        @Query("lang") lang: String?,
        @Query("unit") unit: String?,
        @Header(CustomHeader.cacheTime) cacheTime: Long?
    ): Call<DayWeather>

    @GET("v7/grid-weather/3d")
    fun getGridDayWeather3d(
        @Query("location") location: String,
        @Query("lang") lang: String?,
        @Query("unit") unit: String?,
        @Header(CustomHeader.cacheTime) cacheTime: Long?
    ): Call<DayWeather>

    @GET("v7/weather/7d")
    fun getDayWeather7d(
        @Query("location") location: String,
        @Query("lang") lang: String?,
        @Query("unit") unit: String?,
        @Header(CustomHeader.cacheTime) cacheTime: Long?
    ): Call<DayWeather>

    @GET("v7/grid-weather/7d")
    fun getGridDayWeather7d(
        @Query("location") location: String,
        @Query("lang") lang: String?,
        @Query("unit") unit: String?,
        @Header(CustomHeader.cacheTime) cacheTime: Long?
    ): Call<DayWeather>

    @GET("v7/weather/15d")
    fun getDayWeather15d(
        @Query("location") location: String,
        @Query("lang") lang: String?,
        @Query("unit") unit: String?,
        @Header(CustomHeader.cacheTime) cacheTime: Long?
    ): Call<DayWeather>

    /**
     * 当天天气指数
     */
    @GET("v7/indices/1d")
    fun getIndices1d(
        @Query("location") location: String,
        @Query("lang") lang: String?,
        @Query("type") type :String,
        @Header(CustomHeader.cacheTime) cacheTime: Long?
    ): Call<IndicesEntity>

    /**
     * 实时空气质量
     */
    @GET("v7/air/now")
    fun getDailyAir(
        @Query("location") location: String,
        @Query("lang") lang: String?,
        @Header(CustomHeader.cacheTime) cacheTime: Long?
    ): Call<DailyAirEntity>
    /**
     * 5天空气质量
     */
    @GET("v7/air/5d")
    fun getDayAir(
        @Query("location") location: String,
        @Query("lang") lang: String?,
        @Header(CustomHeader.cacheTime) cacheTime: Long?
    ): Call<DayAirEntity>

}