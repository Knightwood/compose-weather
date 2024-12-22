package com.kiylx.libx.http.ui.flow

import android.util.Log
import com.kiylx.libx.http.ui.UiState
import com.kiylx.libx.http.response.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * activity或fragment中： 可以单独观察数据或界面状态，也可以只观察界面状态，数据手动获取
 *
 * ```
 * lifecycleScope.launch {
 *             mainViewModel.dataState.asUiStateFlow().collect {
 *                 //这里可以观察界面状态
 *                 //界面状态处理...
 *                  if (it is UiState.Success<*>){//success状态下可以拿取服务器返回的数据，可能业务状态不是成功
 *                     val responseData=it.data
 *                 }
 *                 //还可以这样手动获取上此获取成功时设置的最新数据
 *                 mainViewModel.dataState.getData()
 *             }
 *         }
 *         lifecycleScope.launch {
 *             mainViewModel.dataState.asDataFlow().collect {
 *                 //这里可以观察数据变化
 *             }
 *         }
 * ```
 *
 * viewmodel中：
 *
 * ```
 * //界面状态和数据一体的状态实例
 *  var dataState = DataUiState<DailyEntity>()
 *
 *     //网络请求获取数据
 *     suspend fun test(location: Location) {
 *         //1. 接收到网络数据后更新数据和界面状态，网络请求成功后数据会更新，界面状态会更新为成功
 *         //请求失败，数据不会更新，界面状态会更新为失败
 *         val rawResponse = QWeatherRepo.test(location)
 *         dataState.setStateWithData(rawResponse)
 *         //2. 也可以单独更新界面状态，而不更新数据
 *         dataState.setUiState(UiState.Loading)
 *         //3. 也可以单独控制数据和界面的更新
 *         val data=QWeatherRepo.test(location)
 *         if(data.isSuccess()){//判断业务逻辑是否成功，成功则更新数据和界面状态
 *              dataState.setDataOrState(data.responseData,UiState.Success(data))
 *         }else{//传入null的参数不会更新
 *              dataState.setDataOrState(null,UiState.OtherErr(-1,"获取失败"))
 *         }
 *     }
 * ```
 */
open class DataUiState<T>(
    initValue: T,//初始数据
    initUiState: UiState = UiState.INIT,//可选的初始界面状态
) {

    //不论界面状态更新与否，始终在这里保存了一份数据副本
    protected var _data: MutableStateFlow<T> = MutableStateFlow(initValue);

    //界面状态，当然在success状态时会持有一份数据副本
    protected var _uiState: MutableStateFlow<UiState> = MutableStateFlow(initUiState)

    /**
     * 更新数据和界面状态，且如果RawResponse为success，则对_data更新值，否则不更新值
     * 但是在请求成功，但业务code不是成功的这种情况也会更新。
     */
    suspend fun setStateWithData(value: ApiResult<T>) {
        when (value) {
            is ApiResult.ExceptionErr -> {
                val uiState = UiState.RequestErr(value)
                _uiState.emit(uiState)
            }

            is ApiResult.Success -> {
                value.successBody?.let { _data.emit(it) }
                val uiState = UiState.Success(value.successBody)
                _uiState.emit(uiState)
            }
        }
    }

    /**
     * 手动控制数据的更新和界面状态的更新 参数为null时不进行更新
     */
    suspend fun setDataOrState(data: T? = null, uiState: UiState? = null) {
        data?.let {
            _data.emit(it)
        } ?: Log.e(TAG, "data is null,don't emit")

        uiState?.let {
            _uiState.emit(it)
        } ?: Log.e(TAG, "uiState is null,don't emit")
    }

    /**
     * 单独设置数据
     */
    suspend fun setData(data: T) {
        _data.emit(data)
    }

    /**
     * 单独设置界面状态
     */
    suspend fun setUiState(uiState: UiState) {
        _uiState.emit(uiState)
    }

    fun asDataFlow(): StateFlow<T> = _data
    fun asUiStateFlow(): StateFlow<UiState> = _uiState
    fun getData(): T = _data.value
    fun getUiState(): UiState = _uiState.value

    companion object {
        const val TAG = "tty1-DataUiState"
    }
}