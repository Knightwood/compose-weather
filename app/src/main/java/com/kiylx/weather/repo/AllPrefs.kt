package com.kiylx.weather.repo

import android.content.SharedPreferences
import com.kiylx.libx.preferences.delegatetools.saf_prefs.getPreference
import com.kiylx.libx.preferences.delegatetools.saf_prefs.initKey
import com.kiylx.libx.preferences.delegatetools.saf_prefs.int
import com.kiylx.libx.preferences.delegatetools.saf_prefs.string
import com.kiylx.weather.AppCtx
import com.tencent.mmkv.MMKV

class AllPrefs private constructor() {

    var firstEnter =true
    val mmkv =MMKV.defaultMMKV()
//    var firstEnter by mmkv.boolM("firstEnter",true)


    val prefs: SharedPreferences = getPreference(
        AppCtx.instance,
        "sdbfijdaf"
    )

    init {
        prefs.initKey("12345678910abcde")
    }

    var payPlan by prefs.int(play_plan, PayPlan.free)

    var apiKey by prefs.string(
        api_key, "88e87e5caa7a4e84a42d9df1dba236db"
    )

    companion object {
        const val api_key: String = "api_key"
        const val play_plan: String = "play_plan"

        private var repoInstance: AllPrefs? = null
            get() {
                if (field == null) {
                    field = AllPrefs()
                }
                return field
            }

        @Synchronized
        fun get(): AllPrefs = repoInstance!!
    }
}
