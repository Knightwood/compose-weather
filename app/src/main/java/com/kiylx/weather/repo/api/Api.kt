package com.kiylx.weather.repo.api

import com.kiylx.weather.repo.bean.DailyEntity
import retrofit2.Call
import retrofit2.http.GET
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
    ): Call<DailyEntity>

    @GET("v7/grid-weather/now")
    fun getGridDaily(
        @Query("location") location: String,
        @Query("lang") lang: String?,
        @Query("unit") unit: String?,
    ): Call<DailyEntity>
}