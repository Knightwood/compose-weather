package com.kiylx.libx.tools.sonser.gps

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.location.*
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationListenerCompat
import com.kiylx.libx.tools.sonser.gps.convert.egm96.Geoid
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.CompletableFuture

const val tag = "GPS_HOLDER"

/**
 * # 先检查权限，并赋予权限后再使用此类
 * 1. 先调用[GpsHolder.Instance]获取实例。
 * 2. 然后调用[configGps]方法配置。
 * 4. 最终调用[registerListener]即可开始获取位置更新信息
 */
@SuppressLint("MissingPermission")
class GpsHolder private constructor(){
    var running =false
    
    /**
     * 接收位置更新
     */
    private var locationChangedListener: LocationListener =
        LocationListenerCompat { location ->
            Log.d(tag, "gps位置:${location.longitude}-${location.latitude}")
            config.myLocationListener?.locationChanged(this@GpsHolder.dataHolder, location)
        }
    private lateinit var ctx : Application

    private var config: Config = Config().default()
    private var lm: LocationManager? = null
    private var bestProvider: String? = null
    private var egm96InitFlag = false;
    private var hasPerms: Boolean = true
    private var hasInited = false;//是否初始化完成
    private var geoidFileInitIng = false//转换文件是否在初始化
    private val dataHolder: DataHolder by lazy { DataHolder() }//承担location数据的转换处理


    fun configGps(ctx:Application,block: Config.() -> Unit): GpsHolder {
        this.ctx=ctx
        block(config)
        initManager()
        return this
    }


    fun getLastLocation(): Location? {
        return lm?.getLastKnownLocation(bestProvider!!)
    }


    /**
     *绑定监听，有4个参数
     * 参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种
     * 参数2，位置信息更新周期，单位毫秒
     * 参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息
     * 参数4，监听
     * 备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新

     * 2秒更新一次，或最小位移变化超过1米更新一次；
     * 注意：此处更新准确度非常低，推荐在service里面启动一个Thread，在run中sleep(10000);然后执行handler.sendMessage(),更新位置
     */
    private fun requestUpdate() {
        Log.d(tag, "请求位置更新")
        lm?.registerGnssStatusCallback(config.gnssStatusCallback, null)
        lm?.requestLocationUpdates(
            this.bestProvider!!,
            config.requestUpdateInterval,
            config.requestUpdateDistanceInterval,
            locationChangedListener
        )
    }

    //打开位置信息设置页面让用户自己设置
    private fun openGPS2() {
        Toast.makeText(ctx,"需要打开gps的高精度选项",Toast.LENGTH_LONG).show()
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent)
    }

    /**
     * 特定的gps是否启用
     */
    private fun isGpsAble(string: String): Boolean {
        return lm?.isProviderEnabled(string) ?: false
    }

    private fun initManager() {
        if (ActivityCompat.checkSelfPermission(
                ctx,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                ctx,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(tag, "没有权限")
            hasPerms = false
            hasInited = false
            return;
        } else {
            hasPerms = true
        }
        if (!hasInited) {
            lm = ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            var b = false//先判断gps是否启用
            for (s in config.useWhatGps) {
                if (isGpsAble(s)) {
                    b = true
                    break
                }
            }
            if (b) {
                Log.d(tag, "初始化")
                // 为获取地理位置信息时设置查询条件
                bestProvider = if (config.usefused){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        LocationManager.FUSED_PROVIDER
                    }else{
                        lm!!.getBestProvider(config.criteria, true)
                    }
                }else{
                    lm!!.getBestProvider(config.criteria, true)
                }
                //初始化高程转换文件
                if (config.convertAltitude)
                    initGeoid(ctx)
            } else {
                openGPS2()
            }
            hasInited = true
        }
    }

    /**
     * 初始化高程转换器
     * 应该在子线程里初始化
     */
    private fun initGeoid(context: Context) {
        if (geoidFileInitIng) {
            return
        } else {
            geoidFileInitIng = true
            val assetManager: AssetManager = context.assets // get assertManager
            var inputStream: InputStream? = null
            try {
                CompletableFuture.runAsync {
                    inputStream = assetManager.open(config.egmFileName)
                    egm96InitFlag = Geoid.init(inputStream)
                    Log.d(tag,"高程转换初始化：$egm96InitFlag")
                }.whenComplete { _, _ ->
                    geoidFileInitIng = false
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    }

    fun registerListener() {
        if (!hasPerms) {
            return
        }
        Log.d(tag, "注册监听")
        requestUpdate()
        running=true
    }

    fun unRegisterListener() {
        if (!hasPerms) {
            return
        }
        Log.d(tag, "取消注册监听")
        lm?.unregisterGnssStatusCallback(config.gnssStatusCallback)
        lm?.removeUpdates(locationChangedListener)
        config.myLocationListener = null
        running=false
    }

    /**
     * 定位相关的配置
     */
    inner class Config {
        /**
         * assertManager打开此文件，初始化高程转换工具。
         * 更改名称以加载其他转换文件
         */
        var egmFileName: String = "egm96-delta.dat"

        /**
         * 字段在LocationManager中定义。
         * 这是一个数组，会自动遍历数组以此判断有没有指定的位置信息提供，
         * 如果数组中有匹配的，就算是gps被启用了。
         * 默认值：
         * @see android.location.LocationManager.GPS_PROVIDER
         */
        var useWhatGps: Array<String> = arrayOf(LocationManager.GPS_PROVIDER)

        /**
         * 单位是米
         * 指定移动这些距离后，gps才会请求下一次位置更新
         * 如果为0F，则以requestUpdateInterval参数取请求位置更新
         */
        var requestUpdateDistanceInterval: Float = 2F

        /**
         * 单位是毫秒
         * 指定这些时间间隔后更新下一次位置信息。
         * 如果requestUpdateInterval和requestUpdateDistanceInterval都为0，则会随时刷新
         */
        var requestUpdateInterval: Long = 5000L

        /**
         * true:将gps的大地高程转换成海拔高程
         * false:使用gps的大地高程
         */
        var convertAltitude: Boolean = false

        var myLocationListener: MyLocationListener? = null

        var gnssStatusCallback: GnssStatus.Callback = object : GnssStatus.Callback() {
            override fun onStarted() {
                super.onStarted()
                Log.d(tag, "START")
                //第一次获得定位会很慢，所以先使用上一次的定位信息
                //但如果跟上次地点距离很远，会显得非常不准确
                 getLastLocation()?.let {
                     myLocationListener?.locationChanged(
                         dataHolder,
                         it
                     )
                 }
                Toast.makeText(ctx,"正在搜索卫星",Toast.LENGTH_LONG).show()
            }

            override fun onStopped() {
                super.onStopped()
                Log.d(tag, "STOP")
            }

            override fun onFirstFix(ttffMillis: Int) {
                super.onFirstFix(ttffMillis)
                Log.d(tag, "FIRST")
            }

            override fun onSatelliteStatusChanged(status: GnssStatus) {
                super.onSatelliteStatusChanged(status)
            }
        }

        /**
         * 是否使用融合定位，如果为true，则获取定位时融合定位将取代criteria
         * false时使用criteria而不是用融合定位
         * 要求api在31以上才会有用
         */
        var usefused :Boolean =false

        var criteria: Criteria = Criteria().apply {
            // 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
            accuracy = Criteria.ACCURACY_COARSE
            // 设置是否要求速度
            isSpeedRequired = false
            // 设置是否允许运营商收费
            isCostAllowed = false
            // 设置是否需要方位信息
            isBearingRequired = false
            // 设置是否需要海拔信息
            isAltitudeRequired = true
            // 设置对电源的需求
            powerRequirement = Criteria.POWER_MEDIUM
        }

        fun default(): Config {
            return Config()
        }

    }

    /**
     * 对location数据做额外处理
     */
    inner class DataHolder() {
        /**
         * 根据location返回高程异常，如果转换文件没有初始化，初始化转换文件的同时返回0.0
         */
        fun convertAltitude(location: Location): Double {
            return if (egm96InitFlag) {
                Geoid.getOffset(
                    location.latitude,
                    location.longitude
                )
            } else {
                initGeoid(ctx)
                0.0
            }
        }

        /**
         * 限制小数点位数，同时可以转换为度角分的格式
         * @param isConvertUnit true:将坐标转换成度.角.分
         */
        fun friendlyLatlon(location: Location, isConvertUnit: Boolean = false): LatlonPair {
            return LatlonPair.convert(latlon = location, isConvertUnit)
        }
    }

    companion object {
        @JvmStatic
        val Instance: GpsHolder by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            GpsHolder()
        }
    }
}

interface MyLocationListener {
    fun locationChanged(holder: GpsHolder.DataHolder, location: Location)
}