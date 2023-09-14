package com.kiylx.libx.http.kotlin.basic3

import com.kiylx.libx.http.kotlin.common.BaseErrorHandler
import com.kiylx.libx.http.kotlin.common.RawResponse
import com.kiylx.libx.http.kotlin.common.RequestHandler
import kotlinx.coroutines.CoroutineScope
import retrofit2.Call


suspend inline fun <reified T : Any> handleApi2(action: Call<T>): RawResponse<T> {
    return handleApi2(action, null)
}

suspend inline infix fun <reified T : Any> CoroutineScope.handleApi2With(action: Call<T>): RawResponse<T> {
    return handleApi2(action, null)
}

suspend inline fun <reified T : Any, reified E : BaseErrorHandler> handleApi2(
    action: Call<T>,
    errorHandler: E?,
): RawResponse<T> {
    return RequestHandler.handle(action, errorHandler)
}

