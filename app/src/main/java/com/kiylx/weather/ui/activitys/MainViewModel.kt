package com.kiylx.weather.ui.activitys

import androidx.lifecycle.ViewModel
import com.kiylx.libx.http.kotlin.basic3.DataUiState
import com.kiylx.libx.http.kotlin.basic3.UiState
import com.kiylx.libx.http.kotlin.common.RawResponse
import com.kiylx.weather.repo.QWeatherRepo
import com.kiylx.weather.repo.bean.DailyEntity
import com.kiylx.weather.repo.bean.Location

class MainViewModel : ViewModel() {
    /**
     * get weather info and update UiState
     */
    suspend fun getDailyData(data: DataUiState<DailyEntity>, location: Location,noCache:Boolean=false) {
        data.setUiState(UiState.Loading)
        when (val response = QWeatherRepo.getDailyReport(location,noCache=noCache)) {
            is RawResponse.Error -> {
                data.setUiState(UiState.RequestErr(response))
            }

            is RawResponse.Success -> {
                if (response.responseData?.code == "200") {
                    data.setDataOrState(
                        response.responseData,
                        UiState.Success(response.responseData)
                    )
                } else {
                    data.setUiState(
                        UiState.OtherErr(
                            response.responseData?.code?.toInt(),
                        )
                    )
                }
            }
        }
    }

}