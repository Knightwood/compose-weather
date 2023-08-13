package com.kiylx.libx.http.okhttp_logger

import okhttp3.Request
import java.io.IOException

/**
 * @author ihsan on 8/12/18.
 * @author kiylx on 2023-03-04
 */
interface BufferListener {
    @Throws(IOException::class)
    fun getJsonResponse(request: Request?): String?
}