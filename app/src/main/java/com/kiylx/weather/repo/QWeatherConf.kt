package com.kiylx.weather.repo

class PayPlan {
    companion object {
        const val free = 1
        const val payPlan = 2
    }
}

object QWeatherConf {
    val planConf: Int = AllPrefs.get().payPlan

    val geoBaseUrl="https://geoapi.qweather.com/"

    val baseUrl: String
        get() {
            return if (planConf == PayPlan.free) {
                "https://devapi.qweather.com/"
            } else{
                "https://api.qweather.com/"
            }
        }

    val key =AllPrefs.get().apiKey
}