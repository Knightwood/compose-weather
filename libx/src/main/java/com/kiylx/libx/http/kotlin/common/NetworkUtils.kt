package com.kiylx.libx.http.kotlin.common

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.URLConnection
import java.util.regex.Matcher
import java.util.regex.Pattern

object NetworkUtils {
    private const val TAG = "tty2-NetworkUtils"
    private var ip: String = ""//外网ip

    /**
     * 获取外网的IP(要访问Url，要放到后台线程里处理)
     *
     * @param @return
     * @return String
     * @throws
     * @Title: GetNetIp
     * @Description:
     */
    suspend fun getNetExtraNetIpAddress(): String = withContext(Dispatchers.IO) {
        return@withContext kotlin.runCatching {
            if (ip.isEmpty()) {
                val infoUrl: URL
                var inStream: InputStream? = null
                var ipLine = ""
                var httpConnection: HttpURLConnection? = null
                try {
                    infoUrl = URL("https://www.taobao.com/help/getip.php")
                    httpConnection = infoUrl.openConnection() as HttpURLConnection
                    val responseCode: Int = httpConnection.responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        inStream = httpConnection.inputStream
                        val reader = BufferedReader(
                            InputStreamReader(inStream, "utf-8")
                        )
                        val sb = StringBuilder()
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            line?.let {
                                sb.append("$it \n")
                            }
                        }
                        val pattern: Pattern =
                            Pattern.compile("((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))")
                        val matcher: Matcher = pattern.matcher(sb.toString())
                        if (matcher.find()) {
                            ipLine = matcher.group()
                        }
                    }
                } catch (e: MalformedURLException) {
                    Log.e(TAG, "getNetExtraNetIpAddress", e)
                } catch (e: IOException) {
                    Log.e(TAG, "getNetExtraNetIpAddress", e)
                } finally {
                    try {
                        inStream?.close()
                        httpConnection?.disconnect()
                    } catch (e: IOException) {
                        Log.e(TAG, "getNetExtraNetIpAddress", e)
                    } catch (ex: Exception) {
                        Log.e(TAG, "getNetExtraNetIpAddress", ex)
                    }
                }
                ip = ipLine
                return@runCatching ipLine
            } else {
                return@runCatching ip
            }
        }.getOrDefault("127.0.0.1")
    }

}