package com.kiylx.libx.http.kotlin.basic

import com.kiylx.libx.http.kotlin.basic2.LocalError

/**
 * 存储本地错误类型，可以调用对应方法生成Resource实例，放进livedata中，以使界面更新
 */
class LocalErrorModel {
    companion object {
        fun fileSaveFailed() = Resource.failed(LocalError(1, "文件存储失败"))
        fun noFile() = Resource.failed(LocalError(2, "没有文件"))
    }
}