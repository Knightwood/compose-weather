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
import java.util.UUID

/**
 * 将数据存储到磁盘
 * 将文件从磁盘读取
 */
object LocalFile {
    const val TAG = "tty1-LocalFile"

    //存储位置
    val dirPath = AppCtx.instance.externalCacheDir!!.absolutePath
    var locationDir = dirPath + File.separator + "location" + File.separator
    var weatherDir = dirPath + File.separator + "weather" + File.separator

    //命名前缀及后缀
    //位置文件后缀
    val locationSuffix = ".location"

    //天气文件后缀
    val weatherSuffix = ".weather"

    //默认位置文件及默认位置天气文件的前缀
    val default_prefix = "d_"

    /**
     * 如果是默认位置，返回以经纬度为名的文件名
     * 其他的，返回以[Location.id]为名的文件名
     */
    fun genLocationFileName(location: Location): String {
        return if (location.default) {
            default_prefix + location.lat + "_" + location.lon + locationSuffix
        } else {
            location.id + locationSuffix
        }
    }

    fun genWeatherFileName(default: Boolean = false): String {
        return if (default) {
            default_prefix + UUID.randomUUID() + weatherSuffix
        } else {
            UUID.randomUUID().toString() + weatherSuffix
        }
    }

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
                    out.write(Json.encodeToString(location).toByteArray())
                    out.flush()
                }
            }
        }
    }

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
                                val data = Json.decodeFromStream<Location>(it)
                                list.add(data)
                            }
                        }
                    }
                }
                func(list)
            }
        }
    }

    fun readWeather(): List<WeatherSub> {
        return emptyList()
    }

    fun writeWeather(weatherSub: WeatherSub) {

    }

    fun deleteFile(path: String) {
        val file =File(path)
        if (file.exists()){
            file.delete()
        }
    }

}