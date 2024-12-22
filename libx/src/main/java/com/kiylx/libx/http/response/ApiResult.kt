package com.kiylx.libx.http.response

import com.kiylx.libx.http.okhttp3.body.HttpStatus
import com.kiylx.libx.http.okhttp3.body.HttpStatus.getStatusFromCode
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response

/**
 * 2024-11-13：我们处理的有些过于拖沓。 对于请求成功的定义是：只要请求没有爆出异常，导致无法与服务器通讯，就视为成功
 * 所以，这种定义之下，请求的http code为非2XX时，也算作成功。但我们在业务中使用时， 后台绝大多数情况下只会返回2XX的Http
 * Code，是否是成功的业务操作，是放在数据类中的，而不是http code 只有在处理登录、退出时，才会用到非2XX的http
 * code的请求。 因此，我们在用的时候，总是需要在判断业务code之前，多判断一下http code是否时2XX，这其实不方便。
 * 非2XX的 http code 在restful中本来就是请求错误，那些状态码也叫做错误码，指示请求错误原因。
 * 因此，非2XX的http code，我们也应该把他们归类到请求异常中。
 *
 * 对于非2XX http code的请求结果，和2XX的请求结果，但业务code非2XX，哪个处理起来方便？ 非2XX
 * http code的处理，可以使用拦截器，也可以手动在业务中判断处理。 2XX http code，非200
 * 业务code这种，一种是在业务中处理，另一种方案是仿照拦截器，但需要规定请求结果序列化类型是固定的格式。
 * ---
 * 执行网络请求时，包裹成功或失败，异常的原始值 解析call得到response,生成response时成功或捕获到异常,由此产生原始的解析结果
 *
 * 也可以将[ApiResult]进一步处理处理，将返回给网络请求者[Resource]等额外的请求结果
 */
sealed class ApiResult<out T> {
    /**
     * 检查http code，返回response的body，如果http code不是2XX，则返回null
     *
     * 只在http code为2XX时返回response的body。
     *
     * 如果希望不检查http code获取，直接使用[Success.responseBody]
     */
    val successBody: T?
        get() = if (this is Success<T>) {
            response.takeIf { it.isSuccessful }?.body()
        } else null

    /**
     * 非200请求结果（注意，这是http code，不是业务数据的code）时，返回的错误信息。比如请求
     * http code ：502，401之类的。
     * 如果要转为字符串，或转为字符串后使用json库解析为实体。请注意，要使用[string()]，而不要使用[toString]
     *
     * ```
     * val errorBody = response.errorBody()?.string()
     * ```
     */
    val errorBody: ResponseBody?
        get() = if (this is Success<T>) {
            response.takeIf { !it.isSuccessful }?.errorBody()
        } else null

    /**
     * 如果是[Success]类型，则检查http code 以及额外检查check，并返回response
     * 如果是[ExceptionErr]，则返回null
     *
     * 如果不需要额外的检查，直接使用[successBody]即可
     *
     * @param check 检查请求得到的数据是否满足要求
     */
    open infix fun bodyCheckWith(check: (data: T) -> Boolean): T? {
        return successBody?.takeIf(check)
    }

    /**
     * 请求成功，http code 不一定是200，需要自行判定。 也就是说，只要请求过程中没有报错发生异常，则一定是成功的。
     *
     * 比如502 bad gateway时，虽然http code是502，但没有异常，请求肯定是成功的。
     *
     * 要获取错误信息，请使用[errMessage]和[parseError]，这两个，可以解析请求成功时请求的信息，或者请求爆出异常时的异常信息。
     *
     * 或者，你可以读取[httpCode]和[httpMessage]，这两个是请求的原始信息，比如http code，http message等。
     *
     * 又或者，你可以使用[errorBody]自己解析错误。
     *
     * @param response retrofit2中的Response [retrofit2.Response]
     */
    class Success<out T>(
        val response: Response<out T>,
    ) : ApiResult<T>() {
        val headers = response.headers()

        /**
         * 返回okhttp3的Response [okhttp3.Response]
         */
        val raw get() = response.raw()

        /**
         * response body
         */
        val responseBody get() = response.body()

        /**
         * http code
         */
        val httpCode get() = response.code()

        /**
         * response message 这里我们没有解析[errorBody]，而是解析的原始请求response的信息。
         * 至于errBody，你可以自己解析一下.
         * 特别值得注意的是，获取errorBody的字符串一定要用string()，千万千万不要用toString()。
         *
         * ```
         * val errorBody = errorBody()?.string()
         * ```
         */
        val httpMessage: String
            get() {
                var s = response.message()
                if (s.isEmptyOrBlank())
                    s = getStatusFromCode(
                        httpCode
                    ).description
                return s
            }

        val isSuccessful get() = response.isSuccessful

        infix fun checkWith(check: (data: T) -> Boolean): T? {
            return response.takeIf { it.isSuccessful }?.body()?.takeIf(check)
        }
    }

    /**
     * 网络请求爆出异常、请求的http code非2XX
     *
     * @property exception 异常信息
     * @property errBodyString 非2XX请求时得到的errBody。其结构需要后端来约定。
     */
    data class ExceptionErr(
        val exception: Throwable? = null,
    ) : ApiResult<Nothing>() {

        /**
         * 可以获得错误信息，默认是异常的信息
         */
        var errorMessage: String = parseMsg(exception)

        /**
         * 请求为非2XX时，读取了请求的errorBody。
         * 具体的内容需要后端约定。
         */
        val errBodyString: String? = if (exception is HttpException) {
            exception.response()?.errorBody()?.string()
        } else null

        val statusCode: Int?
            get() {
                if (exception is HttpException) {
                    return exception.response()?.code()
                }
                return null
            }

        constructor(exception: Throwable, msg: String) : this(exception) {
            this.errorMessage = msg
        }

        private fun parseMsg(exception: Throwable?): String {
            return when (exception) {
                is HttpException -> {
                    //http status message有可能为空
                    //如果没有http status message，就从http status code中解析
                    val tmp = exception.message()
                    if (tmp.isEmptyOrBlank()) {
                        return HttpStatus.getStatusFromCode(exception.code()).description
                    } else {
                        tmp
                    }
                }
                else -> {
                    exception?.localizedMessage ?: "未知错误"
                }
            }
        }
    }

    companion object {
        /**
         * 解析异常，获取到更人性化的错误提示
         */
        fun ApiResult.ExceptionErr.humanizedCause(): String {
            return exception?.let { exception ->
                when (exception) {
                    is HttpException -> {
                        val tmp = exception.message()
                        if (tmp.isEmptyOrBlank()) {
                            return HttpStatus.getStatusFromCode(exception.code()).description
                        } else {
                            tmp
                        }
                    }

                    is java.net.SocketTimeoutException -> {
                        "网络连接超时，请稍后再试"
                    }

                    is java.net.ConnectException -> {
                        "网络连接失败，请稍后再试"
                    }

                    is okhttp3.internal.http2.ConnectionShutdownException -> {
                        "连接已关闭，请稍后再试"
                    }

                    is kotlinx.serialization.SerializationException -> {
                        "反序列化失败"
                    }

                    else -> {
                        "网络开小差了，请稍后再试"
                    }
                }
            } ?: "未知错误"
        }
    }

}

fun String?.isEmptyOrBlank(): Boolean {
    return isNullOrEmpty() || isBlank()
}