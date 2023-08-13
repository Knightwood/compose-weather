package com.kiylx.libx.http.kotlin.basic

/**
 * 创建者 kiylx
 * 创建时间 2022/5/7 20:09
 * packageName：com.crystal.aplayer.module_base.base.http.okhttp
 * 描述：
 */
enum class Status {
    SUCCESS,//httpcode==200,服务器返回数据
    REQUEST_ERROR,//网络请求失败,httpcode非200,连接超时等

    //    HALF_ERROR,//这可以表示为httpcode是200,但服务器返回的数据中,code非200
    LOADING,
    LOCAL_ERR//本地错误（非网络请求产生的错误）
}