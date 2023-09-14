package com.kiylx.libx.http.kotlin.basic

import com.kiylx.libx.http.kotlin.common.ErrorType.*
import com.kiylx.libx.http.kotlin.common.BaseErrorHandler
import com.kiylx.libx.http.kotlin.common.RawResponse
import com.kiylx.libx.http.kotlin.common.RequestHandler
import kotlinx.coroutines.CoroutineScope
import retrofit2.Call


/*
 *
 * 存有所有的apiService，方便外界通过此类而不是直接持有apiService调用。
 * 比如调用接口时有默认值，在这里传入默认值就不用由外界一层一层的传入。
 *
 * apiService1=ServiceCreator.Service(ApiService1::class.java)
 * 由NetWorkHelper持有所有的apiService，这样就可以用NetWorkHelper.apiServices1取代上面的写法
 *
 * repo= MainRepository(
 *              ApiHelper1(apiService1,ServiceCreator.Service(ApiService2::class.java),...),
 *              ApiHelper2(ServiceCreator.Service(ApiService3::class.java)),
 *              .....
 *      )
 *      在这里可以对得到的数据作出修改，比如从数据库提取数据
 */


/**
 * 很多情况下不用在某些code情况下额外处理
 * 所以可以调用此方法,减少代码量
 */
suspend inline fun <reified T : Any> handleApi(action: Call<T>): Resource<T> {
    return handleApi(action, null)
}

suspend inline infix fun <reified T : Any> CoroutineScope.handleApiWith(action: Call<T>): Resource<T> {
    return handleApi(action, null)
}

/**
 * @param action retrofit执行得到的Call<T>
 *@param block 令调用者可以判断服务器的具体返回结果，做出额外处理，
 */
suspend inline fun <reified T : Any, reified E : BaseErrorHandler> handleApi(
    action: Call<T>,
    errorHandler: E? = null,
): Resource<T> {
    return when (val rawResponse: RawResponse<T> = RequestHandler.handle(action, errorHandler)) {
        is RawResponse.Error -> {
            Resource.error(rawResponse)
        }

        is RawResponse.Success -> {
            val info = rawResponse.responseData
            Resource.success(info)
        }

        else -> {
            throw IllegalArgumentException("参数类型不允许")
        }
    }
}


