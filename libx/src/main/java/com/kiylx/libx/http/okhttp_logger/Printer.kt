package com.kiylx.libx.http.okhttp_logger

import android.text.TextUtils
import okhttp3.*
import okhttp3.internal.http.promisesBody
import okio.Buffer
import okio.GzipSource
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.EOFException
import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * @author ihsan on 09/02/2017.
 * @author kiylx on 2023-03-04
 */
class Printer private constructor() {
    companion object {
        private const val JSON_INDENT = 3
        private val LINE_SEPARATOR: String = System.getProperty("line.separator")
        private val DOUBLE_SEPARATOR = LINE_SEPARATOR + LINE_SEPARATOR
        private const val N = "\n"
        private const val T = "\t"
        private const val REQUEST_UP_LINE =
            "┌────── Request ────────────────────────────────────────────────────────────────────────"
        private const val END_LINE =
            "└───────────────────────────────────────────────────────────────────────────────────────"
        private const val RESPONSE_UP_LINE =
            "┌────── Response ───────────────────────────────────────────────────────────────────────"
        private const val BODY_TAG = "Body:"
        private const val URL_TAG = "URL: "
        private const val METHOD_TAG = "Method: @"
        private const val HEADERS_TAG = "Headers:"
        private const val STATUS_CODE_TAG = "Status Code: "
        private const val RECEIVED_TAG = "Received in: "
        private const val DEFAULT_LINE = "│ "
        private val OOM_OMITTED = LINE_SEPARATOR + "Output omitted because of Object size."
        private fun isEmpty(line: String): Boolean {
            return line.isEmpty() || N == line || T == line || line.trim { it <= ' ' }.isEmpty()
        }

        /**
         * 打印请求
         */
        fun printJsonRequest(
            builder: LoggingInterceptor.Builder,
            body: RequestBody?,
            url: String,
            header: Headers,
            method: String,
        ) {
            val requestBody = body?.let {
                LINE_SEPARATOR + BODY_TAG + LINE_SEPARATOR + bodyToString(body, header)
            } ?: ""
            val msgBody: StringBuilder = StringBuilder()

            val topTag = builder.getTag(true)
            var tag = ""
            if (builder.singleTag) {
                msgBody.append(" 请求开始 \n")
            } else {
                tag = topTag
            }
            if (builder.logger == null) {
                msgBody.append("$tag $REQUEST_UP_LINE \n")
            }
            //url
            msgBody.append(
                logLines(tag, arrayOf(URL_TAG + url), false)
            )
            //header and method
            msgBody.append(
                logLines(tag, getRequest(builder.level, header, method), true)
            )

            if (builder.level == Level.BASIC || builder.level == Level.BODY) {
                //requestbody 字符串
                msgBody.append(
                    logLines(tag, requestBody.split(LINE_SEPARATOR).toTypedArray(), true)
                )
            }
            //结束行
            if (builder.logger == null) {
                msgBody.append("$tag $END_LINE ")
            }
            if (builder.logger == null) {
                I.log(builder.type, topTag, msgBody.toString(), builder.isLogHackEnable)
            } else {
                builder.logger!!.log(builder.type, topTag, msgBody.toString())
            }
        }

        fun printJsonResponse(
            builder: LoggingInterceptor.Builder,
            chainMs: Long,
            isSuccessful: Boolean,
            code: Int,
            headers: Headers,
            response: Response,
            segments: List<String>,
            message: String,
            responseUrl: String,
        ) {
            val responseBody =
                LINE_SEPARATOR + BODY_TAG + LINE_SEPARATOR + responseBodyToString(response)
            val topTag = builder.getTag(false)
            var tag = ""
            val msgBody: StringBuilder = StringBuilder()
            if (builder.singleTag) {
                msgBody.append(" 回应开始 \n")
            } else {
                tag = topTag
            }
            val urlLine = arrayOf(URL_TAG + responseUrl)
            //response string
            val responseString =
                getResponse(headers, chainMs, code, isSuccessful, builder.level, segments, message)

            if (builder.logger == null) {
                msgBody.append("$tag $RESPONSE_UP_LINE \n")
            }
            msgBody.append(logLines(tag, urlLine, true))
            msgBody.append(logLines(tag, responseString, true))
            if (builder.level == Level.BASIC || builder.level == Level.BODY) {
                //response body
                msgBody.append(
                    logLines(tag, responseBody.split(LINE_SEPARATOR).toTypedArray(), true)
                )
            }
            if (builder.logger == null) {
                msgBody.append("$tag $END_LINE ")
            }
            if (builder.logger == null) {
                I.log(builder.type, topTag, msgBody.toString(), builder.isLogHackEnable)
            } else {
                builder.logger!!.log(builder.type, topTag, msgBody.toString())
            }
        }

        private fun responseBodyToString(response: Response): String {
            val responseBody = response.body!!
            val headers = response.headers
            val contentLength = responseBody.contentLength()
            if (!response.promisesBody()) {
                return "End request - Promises Body"
            } else if (bodyHasUnknownEncoding(response.headers)) {
                return "encoded body omitted"
            } else {
                val source = responseBody.source()
                source.request(Long.MAX_VALUE) // Buffer the entire body.
                var buffer = source.buffer

                var gzippedLength: Long? = null
                if ("gzip".equals(headers["Content-Encoding"], ignoreCase = true)) {
                    gzippedLength = buffer.size
                    GzipSource(buffer.clone()).use { gzippedResponseBody ->
                        buffer = Buffer()
                        buffer.writeAll(gzippedResponseBody)
                    }
                }

                val contentType = responseBody.contentType()
                val charset: Charset = contentType?.charset(StandardCharsets.UTF_8)
                    ?: StandardCharsets.UTF_8

                if (!buffer.isProbablyUtf8()) {
                    return "End request - binary ${buffer.size}:byte body omitted"
                }

                if (contentLength != 0L) {
                    return getJsonString(buffer.clone().readString(charset))
                }

                return if (gzippedLength != null) {
                    "End request - ${buffer.size}:byte, $gzippedLength-gzipped-byte body"
                } else {
                    "End request - ${buffer.size}:byte body"
                }
            }
        }

        private fun getRequest(level: Level, headers: Headers, method: String): Array<String> {
            val log: String
            val loggableHeader = level == Level.HEADERS || level == Level.BASIC
            log = METHOD_TAG + method + DOUBLE_SEPARATOR +
                    if (isEmpty("$headers")) "" else if (loggableHeader) HEADERS_TAG + LINE_SEPARATOR + dotHeaders(
                        headers) else ""
            return log.split(LINE_SEPARATOR).toTypedArray()
        }

        /**
         * response but not response body
         */
        private fun getResponse(
            headers: Headers, tookMs: Long, code: Int, isSuccessful: Boolean,
            level: Level, segments: List<String>, message: String,
        ): Array<String> {
            val log: String
            val loggableHeader = level == Level.HEADERS || level == Level.BASIC
            val segmentString = slashSegments(segments)
            log = ((if (segmentString.isNotEmpty()) "$segmentString - " else "") + "[is success : "
                    + isSuccessful + "] - " + RECEIVED_TAG + tookMs + "ms" + DOUBLE_SEPARATOR + STATUS_CODE_TAG +
                    code + " / " + message + DOUBLE_SEPARATOR + when {
                isEmpty("$headers") -> ""
                loggableHeader -> HEADERS_TAG + LINE_SEPARATOR +
                        dotHeaders(headers)
                else -> ""
            })
            return log.split(LINE_SEPARATOR).toTypedArray()
        }

        private fun slashSegments(segments: List<String>): String {
            val segmentString = StringBuilder()
            for (segment in segments) {
                segmentString.append("/").append(segment)
            }
            return segmentString.toString()
        }

        private fun dotHeaders(headers: Headers): String {
            val builder = StringBuilder()
            headers.forEach { pair ->
                builder.append("${pair.first}: ${pair.second}").append(N)
            }
            return builder.dropLast(1).toString()
        }

        private fun logLines(
            tag: String, lines: Array<String>, withLineSize: Boolean,
        ): String {
            val sb = StringBuilder()
            for (line in lines) {
                val lineLength = line.length
                val maxLogSize = if (withLineSize) 110 else lineLength
                for (i in 0..lineLength / maxLogSize) {
                    val start = i * maxLogSize
                    var end = (i + 1) * maxLogSize
                    end = if (end > line.length) line.length else end
                    sb.append("$tag $DEFAULT_LINE ${line.substring(start, end)} \n")
                }
            }
            return sb.toString()
        }

        /**
         * RequestBody 转换成字符串
         */
        private fun bodyToString(requestBody: RequestBody?, headers: Headers): String {
            return requestBody?.let {
                return try {
                    if (bodyHasUnknownEncoding(headers)) {
                        return "encoded body omitted)"
                    } else if (requestBody.isDuplex()) {
                        return "duplex request body omitted"
                    } else if (requestBody.isOneShot()) {
                        return "one-shot body omitted"
                    } else {
                        val paramString: String
                        if (requestBody is MultipartBody) { //判断是否有文件
                            val sb = java.lang.StringBuilder()
                            val parts = requestBody.parts
                            var partBody: RequestBody
                            var i = 0
                            val size = parts.size
                            while (i < size) {
                                partBody = parts[i].body
                                if (sb.isNotEmpty()) {
                                    sb.append(",")
                                }
                                if (isPlainText(partBody.contentType())) {
                                    sb.append(readContent(partBody))
                                } else {
                                    sb.append("other-param-type=")
                                        .append(partBody.contentType())
                                }
                                i++
                            }
                            paramString = sb.toString()
                            return paramString
                        } else {
                            val buffer = Buffer()
                            requestBody.writeTo(buffer)

                            val contentType = requestBody.contentType()
                            val charset: Charset = contentType?.charset(StandardCharsets.UTF_8)
                                ?: StandardCharsets.UTF_8

                            if (buffer.isProbablyUtf8()) {
                                return getJsonString(buffer.readString(charset)) + LINE_SEPARATOR + "${requestBody.contentLength()}-byte body"
                            } else {
                                return "binary ${requestBody.contentLength()}-byte body omitted"
                            }
                        }
                    }
                } catch (e: IOException) {
                    "{\"err\": \"" + e.message + "\"}"
                }
            } ?: ""
        }

        private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
            val contentEncoding = headers["Content-Encoding"] ?: return false
            return !contentEncoding.equals("identity", ignoreCase = true) &&
                    !contentEncoding.equals("gzip", ignoreCase = true)
        }

        private fun getJsonString(msg: String): String {
            val message: String = try {
                when {
                    msg.startsWith("{") -> {
                        val jsonObject = JSONObject(msg)
                        jsonObject.toString(JSON_INDENT)
                    }
                    msg.startsWith("[") -> {
                        val jsonArray = JSONArray(msg)
                        jsonArray.toString(JSON_INDENT)
                    }
                    else -> {
                        msg
                    }
                }
            } catch (e: JSONException) {
                msg
            } catch (e1: OutOfMemoryError) {
                OOM_OMITTED
            }
            return message
        }

        fun printFailed(tag: String, builder: LoggingInterceptor.Builder) {
            I.log(builder.type, tag, RESPONSE_UP_LINE, builder.isLogHackEnable)
            I.log(builder.type, tag, DEFAULT_LINE + "Response failed", builder.isLogHackEnable)
            I.log(builder.type, tag, END_LINE, builder.isLogHackEnable)
        }

        fun isPlainText(mediaType: MediaType?): Boolean {
            if (null != mediaType) {
                var mediaTypeString = if (null != mediaType) mediaType.toString() else null
                if (!TextUtils.isEmpty(mediaTypeString)) {
                    mediaTypeString = mediaTypeString!!.lowercase(Locale.getDefault())
                    if (mediaTypeString.contains("text") || mediaTypeString.contains("application/json")) {
                        return true
                    }
                }
            }
            return false
        }

        private fun readContent(body: RequestBody?): String? {
            if (body == null) {
                return ""
            }
            val buffer = Buffer()
            try {
                //小于2m
                if (body.contentLength() <= 2 * 1024 * 1024) {
                    body.writeTo(buffer)
                } else {
                    return "content is more than 2M"
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return buffer.readUtf8()
        }
    }

    init {
        throw UnsupportedOperationException()
    }


}

/**
 * @see 'https://github.com/square/okhttp/blob/master/okhttp-logging-interceptor/src/main/java/okhttp3/logging/utf8.kt'
 * */
internal fun Buffer.isProbablyUtf8(): Boolean {
    try {
        val prefix = Buffer()
        val byteCount = size.coerceAtMost(64)
        copyTo(prefix, 0, byteCount)
        for (i in 0 until 16) {
            if (prefix.exhausted()) {
                break
            }
            val codePoint = prefix.readUtf8CodePoint()
            if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                return false
            }
        }
        return true
    } catch (_: EOFException) {
        return false // Truncated UTF-8 sequence.
    }
}