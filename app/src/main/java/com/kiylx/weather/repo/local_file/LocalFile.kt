package com.kiylx.weather.repo.local_file

import com.kiylx.weather.AppCtx
import com.kiylx.weather.repo.WeatherSub
import com.kiylx.weather.repo.bean.Location
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
     * 如果是默认位置，返回"d"为名的文件名，即：d.location
     * 其他的，返回以[Location.id]为名的文件名
     */
    fun genLocationFileName(location: Location): String {
        return if (location.default) {
            default_prefix + locationSuffix
        } else {
            location.id + locationSuffix
        }
    }

    /**
     * 将位置信息写入文件
     */
    fun writeLocation(location: Location) {
        AppCtx.scope.launch {
            withContext(Dispatchers.IO) {
                val parentDir = File(locationDir)
                if (!parentDir.exists()) {
                    parentDir.mkdirs()
                }
                val tmp = locationDir + genLocationFileName(location)
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
    fun readLocations(func: (list: List<Location>) -> Unit) {
        AppCtx.scope.launch {
            withContext(Dispatchers.IO) {
                val dir = File(locationDir)
                val list = mutableListOf<Location>()
                if (dir.exists()) {
                    dir.listFiles()?.forEach {
                        if (it.isFile) {
                            FileInputStream(it).use {
                                val data = json.decodeFromStream<Location>(it)
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
    fun deleteLocation(location: Location) {
        val path = locationDir + genLocationFileName(location)
        deleteFile(path)
    }

//</editor-fold>

//<editor-fold desc="天气信息文件操作">
    /**
     * 默认位置的天气，返回"d"为名的文件名，即：d.weather
     * 其他的，返回以[Location.id]为名的文件名
     */
    fun genWeatherFileName(location: Location): String {
        return if (location.default) {
            default_prefix + weatherSuffix
        } else {
            location.id + weatherSuffix
        }
    }


    @OptIn(ExperimentalSerializationApi::class)
    fun readWeather(func: (list: List<WeatherSub>) -> Unit) {
        AppCtx.scope.launch {
            withContext(Dispatchers.IO) {
                val dir = File(weatherDir)
                val list = mutableListOf<WeatherSub>()
                if (dir.exists()) {
                    dir.listFiles()?.forEach {
                        if (it.isFile) {
                            FileInputStream(it).use {
                                val data = json.decodeFromStream<WeatherSub>(it)
                                list.add(data)
                            }
                        }
                    }
                }
                func(list)
            }
        }
    }

    fun writeWeather(weatherSub: WeatherSub) {
        AppCtx.scope.launch {
            withContext(Dispatchers.IO) {
                val parentDir = File(weatherDir)
                if (!parentDir.exists()) {
                    parentDir.mkdirs()
                }
                val tmp = weatherDir + genWeatherFileName(weatherSub.location)
                val tmpFile = File(tmp)
                if (!tmpFile.exists()) {
                    tmpFile.createNewFile()
                }
                FileOutputStream(tmpFile, false).use { out ->
                    out.write(json.encodeToString(weatherSub).toByteArray())
                    out.flush()
                }
            }
        }

    }

    fun deleteWeather(data: Location) {
        val path = genWeatherFileName(data);
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