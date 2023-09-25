package com.kiylx.libx.mmkv

import android.os.Parcelable
import com.tencent.mmkv.MMKV
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * 使用方式：
 * ```
 * 1.先搞一个mmkv实例
 * val mv = MMKV.defaultMMKV()
 * 2.使用委托的方式生成一个委托对象，除了[parcelableM]方法，初始值可选
 * var example by mv.strM("kk","初始值")
 * 3.使用赋值将值存入
 * example="新的值"
 * 4.直接使用即读取值，如果没有值写入，读取出来的会是默认值。
 * log.d(TAG,example)
 * ```
 *
 */
class MExt

inline fun <T> MMKV.delegate(
    key: String? = null,
    defaultValue: T,
    crossinline getter: MMKV.(String, T) -> T,
    crossinline setter: MMKV.(String, T) -> Boolean
): ReadWriteProperty<Any, T> = object : ReadWriteProperty<Any, T> {

    override fun getValue(thisRef: Any, property: KProperty<*>): T =
        getter(key ?: property.name, defaultValue)

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        setter(key ?: property.name, value)
    }
}

fun MMKV.strM(
    key: String,
    defValue: String = "",
): ReadWriteProperty<Any, String> {
    return delegate<String>(key, defValue, getter = { key1, def ->
        return@delegate this.decodeString(key1, def) ?: def
    }, MMKV::encode)
}

fun MMKV.intM(
    key: String,
    defValue: Int = 0,
): ReadWriteProperty<Any, Int> {
    return delegate<Int>(key, defValue, MMKV::decodeInt, MMKV::encode)
}

fun MMKV.boolM(
    key: String,
    defValue: Boolean = false,
): ReadWriteProperty<Any, Boolean> {
    return delegate(key, defValue, MMKV::decodeBool, MMKV::encode)
}

fun MMKV.longM(
    key: String,
    defValue: Long = 0L,
): ReadWriteProperty<Any, Long> {
    return delegate(key, defValue, MMKV::decodeLong, MMKV::encode)
}

fun MMKV.floatM(
    key: String,
    defValue: Float = 0F,
): ReadWriteProperty<Any, Float> {
    return delegate(key, defValue, MMKV::decodeFloat, MMKV::encode)
}

fun MMKV.doubleM(
    key: String,
    defValue: Double = 0.0,
): ReadWriteProperty<Any, Double> {
    return delegate(key, defValue, MMKV::decodeDouble, MMKV::encode)
}

fun MMKV.bytesM(
    key: String,
    defValue: ByteArray = byteArrayOf(),
): ReadWriteProperty<Any, ByteArray> {
    return delegate(key, defValue, getter = { key1, def ->
        return@delegate this.decodeBytes(key1, def) ?: def
    }, MMKV::encode)
}

inline fun <reified T : Parcelable> MMKV.parcelableM(
    key: String,
    defValue: T,
): ReadWriteProperty<Any, T> {
    return delegate(key, defValue, getter = { key1, def ->
        return@delegate this.decodeParcelable(key1, T::class.java, def)?:def
    }, MMKV::encode)
}


