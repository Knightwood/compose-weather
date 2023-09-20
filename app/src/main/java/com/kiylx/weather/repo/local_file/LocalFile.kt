package com.kiylx.weather.repo.local_file

import com.kiylx.weather.AppCtx
import com.kiylx.weather.repo.bean.LocationEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * 将数据存储到磁盘
 * 将文件从磁盘读取
 */
object LocalFile {
    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    //<editor-fold desc="字段">
    const val TAG = "tty1-LocalFile"

    //存储位置
    val dirPath = AppCtx.instance.externalCacheDir!!.absolutePath
    var locationDir = dirPath + File.separator + "location" + File.separator
    var weatherDir = dirPath + File.separator + "weather" + File.separator

    //命名前缀及后缀
    //位置文件后缀
    val locationSuffix = ".l"

    //天气文件后缀
    val weatherSuffix = ".w"

    //默认位置文件及默认位置天气文件的前缀
    val default_prefix = "d"
//</editor-fold>


//<editor-fold desc="位置信息文件操作">

    /**
     * 将位置信息写入文件
     */
    fun writeLocation(location: LocationEntity,index:Int) {
        AppCtx.scope.launch {
            withContext(Dispatchers.IO) {
                val parentDir = File(locationDir)
                if (!parentDir.exists()) {
                    parentDir.mkdirs()
                }
                val tmp = "$locationDir$index$locationSuffix"
                val tmpFile = File(tmp)
                if (!tmpFile.exists()) {
                    tmpFile.createNewFile()
                }
                FileOutputStream(tmpFile, false).use { out ->
                    out.write(json.encodeToString(location).toByteArray())
                    out.flush()
                }
            }
        }
    }

    /**
     * 将本地文件读取并反序列化
     */
    @OptIn(ExperimentalSerializationApi::class)
    fun readLocations(func: (list: List<LocationEntity>) -> Unit) {
        AppCtx.scope.launch {
            withContext(Dispatchers.IO) {
                val dir = File(locationDir)
                val list = mutableListOf<LocationEntity>()
                if (dir.exists()) {
                    dir.listFiles()?.forEach {
                        if (it.isFile) {
                            FileInputStream(it).use {
                                val data = json.decodeFromStream<LocationEntity>(it)
                                list.add(data)
                            }
                        }
                    }
                }
                func(list)
            }
        }
    }

    /**
     * 将此位置信息的文件删除
     */
    fun deleteLocation(index: Int) {
        val path = "$locationDir$index$locationSuffix"
        deleteFile(path)
    }

//</editor-fold>

    //<editor-fold desc="文件操作">
    fun deleteFile(path: String) {
        val file = File(path)
        if (file.exists()) {
            file.delete()
        }
    }


//</editor-fold>


}