package com.kiylx.compose_lib.util

import com.tencent.mmkv.MMKV

const val LANGUAGE = "language"
const val SYSTEM_DEFAULT = 0

object LangsPref {
    val kv = MMKV.defaultMMKV()

    fun getLanguageConfiguration(languageNumber: Int = kv.decodeInt(LANGUAGE)) =
        languageMap.getOrElse(languageNumber) { "" }


    private fun getLanguageNumberByCode(languageCode: String): Int =
        languageMap.entries.find { it.value == languageCode }?.key ?: SYSTEM_DEFAULT

}

