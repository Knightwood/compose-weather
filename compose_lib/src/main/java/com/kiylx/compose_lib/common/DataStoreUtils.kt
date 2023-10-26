package com.kiylx.compose_lib.common

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * 批量添加或编辑，参数使用 key to value 生成[Preferences.Pair]
 */
suspend inline fun <reified T> DataStore<Preferences>.putAll(vararg pairs: Preferences.Pair<T>) {
    this.updateData {
        it.toMutablePreferences().putAll(*pairs)
        it
    }
}

inline fun <reified T> DataStore<Preferences>.asDataFlow(
    key: Preferences.Key<T>,
    defaultValue: T
): Flow<T> {
    return this.data.map { preferences ->
        // No type safety.
        preferences[key] ?: defaultValue
    }
}

/**
 * 生成一个包装类，方便给dataStore设置值与取值
 */
inline fun <reified T :Any> DataStore<Preferences>.createFor(
    key:Preferences.Key<T>,
    defValue: T,
): DataStoreWrapper<T> {
    return DataStoreWrapper(this@createFor, key, defValue)
}

class DataStoreWrapper<T : Any>(
    private val dataStore: DataStore<Preferences>,
    val key: Preferences.Key<T>,
    val defValue: T,
) {

    fun asFlow(): Flow<T> {
        return dataStore.data.map { preferences ->
            // No type safety.
            preferences[key] ?: defValue
        }
    }

    suspend fun setValue(value: T) {
        dataStore.edit {
            it[key] = value
        }
    }

}

/**
 * 示例
 */
class DataStoreUtils private constructor(val application: Application) {
    val Context.myDataStore by preferencesDataStore("filename")
    val test = application.myDataStore.createFor(intPreferencesKey("HH"), 23)

    companion object {
        @Volatile
        private var utilInstance: DataStoreUtils? = null

        fun instance(application: Application): DataStoreUtils {
            return utilInstance ?: synchronized(this) {
                utilInstance ?: DataStoreUtils(application).also { utilInstance = it }
            }
        }
    }

}
