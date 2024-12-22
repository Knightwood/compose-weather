package com.kiylx.libx.http.okhttp3.okhttp_logger

import okhttp3.internal.platform.Platform
import okhttp3.internal.platform.Platform.Companion.INFO

/**
 * @author ihsan on 11/07/2017.
 * @author kiylx on 2023-03-04
 * 依赖 "com.squareup.okhttp3:logging-interceptor:4.2.0"
 */
interface Logger {
    fun log(level: Int = INFO, tag: String? = null, msg: String? = null)

    companion object {
        val DEFAULT: Logger = object : Logger {
            override fun log(level: Int, tag: String?, msg: String?) {
                //这里的函数调用视okhttp的版本而变化
                //4.12版本： log(level, "$msg", null)
                //4.11及之前 log("$msg", level, null)
                Platform.get().log( level,"$msg", null)
            }
        }
    }
}