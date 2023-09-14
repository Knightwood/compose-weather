package com.kiylx.weather.ui.page.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kiylx.weather.R
import com.kiylx.weather.icon.IconText
import com.kiylx.weather.icon.TwoText
import com.kiylx.weather.icon.WeatherIcon
import com.kiylx.weather.repo.bean.DailyEntity
import com.kiylx.weather.repo.bean.Location

@Composable
fun DailyWeatherInfo(location: Location, dailyEntityState: State<DailyEntity?>) {
    dailyEntityState.value?.let { data ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconText(
                icon = painterResource(WeatherIcon.getResId()),
                title = stringResource(id = R.string.pressure),
                text = data.data.pressure,
            )
            IconText(
                icon = painterResource(WeatherIcon.getResId()),
                title = stringResource(id = R.string.vis),
                text = data.data.vis
            )
        }

        Row {
            TwoText(title = "title", text = "text body")
            TwoText(title = "title", text = "text body")
        }
    }
}
