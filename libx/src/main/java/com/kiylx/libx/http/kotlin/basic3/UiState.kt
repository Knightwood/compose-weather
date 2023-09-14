package com.kiylx.libx.http.kotlin.basic3

import com.kiylx.libx.http.kotlin.common.RawResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class UiState {

    data object INIT : UiState()

    /**
     * 成功，显示界面。也可以携带数据
     */
    data class Success<T>(val data: T? = null) : UiState()

    /**
     * 网络请求错误
     */
    data class RequestErr(
        val requestError: RawResponse.Error? = null
    ) : UiState()

    /**
     * 除了网络请求之外的其他错误
     */
    data class OtherErr(
        val code: Int? = null,
        val msg: String? = null,
        val throwable: Throwable? = null
    ) : UiState()

    data object Loading : UiState()

    data object Empty : UiState()

}

/**
 * activity或fragment中：
 * 可以单独观察数据或界面状态，也可以只观察界面状态，数据手动获取
 * ```
 * lifecycleScope.launch {
 *             mainViewModel.dataState.asUiStateFlow().collect {
 *                 //这里可以观察界面状态
 *                 //界面状态处理...
 *
 *                 //还可以这样手动获取数据
 *                 mainViewModel.dataState.getData()
 *             }
 *         }
 *         lifecycleScope.launch {
 *             mainViewModel.dataState.asDataFlow().collect {
 *                 //这里可以观察数据变化
 *             }
 *         }
 *```
 * viewmodel中：
 * ```
 * //界面状态和数据一体的状态实例
 *  var dataState = DataUiState<DailyEntity>()
 *
 *     //网络请求获取数据
 *     suspend fun test(location: Location) {
 *         //1. 接收到网络数据后更新数据和界面状态，网络请求成功后数据会更新，界面状态会更新为成功
 *         //请求失败，数据不会更新，界面状态会更新为失败
 *         dataState.setData(QWeatherRepo.test(location))
 *         //2. 也可以单独更新界面状态，而不更新数据
 *         dataState.setUiState(UiState.Loading)
 *     }
 * ```
 */
open class DataUiState<T> {
    //不论界面状态更新与否，使用在这里保存了一份数据副本
    protected var _data: MutableStateFlow<T?> = MutableStateFlow(null);
    //界面状态，当然在success状态时会持有一份数据副本
    protected var _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)

    /**
     * 更新数据和界面状态，且如果RawResponse为success，则对_data更新值，否则不更新值
     */
    fun setData(value: RawResponse<T>) {
        when (value) {
            is RawResponse.Error -> {
                val data = UiState.RequestErr(value)
                _uiState.tryEmit(data)
            }

            is RawResponse.Success -> {
                _data.tryEmit(value.responseData)
                _uiState.tryEmit(UiState.Success(value.responseData))
            }
        }
    }

    /**
     * 单独设置界面状态
     */
    fun setUiState(uiState: UiState){
        _uiState.tryEmit(uiState)
    }

    fun asDataFlow(): StateFlow<T?> = _data
    fun asUiStateFlow(): StateFlow<UiState> = _uiState
    fun getData(): T? = _data.value
}
