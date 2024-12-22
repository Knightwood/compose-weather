package com.kiylx.weather.http

import com.kiylx.libx.http.response.ApiResult
import com.kiylx.weather.repo.bean.HttpData



fun HttpData?.isOK(): Boolean {
    return this != null && this.code == "200"
}

//<editor-fold desc="使用HttpData类的请求值检查">
/**
 * 验证返回数据是否成功
 */
fun <T : HttpData> ApiResult.Success<T>.codeIs200(): Boolean {
    return checkWith(::codeCheck) != null
}

/**
 * 可以用于code的检查， 用法：
 *
 * ```
 *
 * val a = Repo.getSomeInfo()
 * a.checkIsSucceed(::OkCheck)?.data
 *
 * ```
 */
fun codeCheck(data: HttpData): Boolean {
    return data.code == "200"
}

//</editor-fold>

//<editor-fold desc="使用HttpData类的请求错误信息解析">

/**
 * 返回可能得错误信息，但如果是请求200，业务数据200，也会返回业务数据中的code和msg。 如果请求过程中爆出异常，则返回异常信息
 * 如果是请求成功，http code是2XX时，返回业务数据的code和msg 如果是请求成功，但http
 * code不是2XX，则返回请求结果的http code和msg
 */
fun ApiResult<HttpData>.errMessage(): Pair<String, String> {
    return when (this) {
        is ApiResult.ExceptionErr -> {
            Pair("-1", this.errorMessage)
        }

        //只要是请求成功，且返回了数据，不论code是多少，这里都拿取数据
        is ApiResult.Success -> {
            successBody?.let {
                Pair(it.code, "ok")
            } ?: Pair(httpCode.toString(), httpMessage)//没有得到body，也就拿不到code，就只好返回请求本身的code和message
        }
    }
}


//</editor-fold>