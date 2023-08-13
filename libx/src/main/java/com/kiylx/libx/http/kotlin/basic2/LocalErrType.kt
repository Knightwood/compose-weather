package com.kiylx.libx.http.kotlin.basic2

/**
 * 生成本地错误
 */
class LocalErrType {
    companion object {
        fun fileSaveFailed() = LocalError(1, "文件存储失败")
        fun noFile() = LocalError(2, "没有文件")
    }
}