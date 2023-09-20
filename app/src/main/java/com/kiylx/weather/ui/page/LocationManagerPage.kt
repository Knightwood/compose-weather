package com.kiylx.weather.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kiylx.compose_lib.component.FloatIconTextButton
import com.kiylx.weather.R
import com.kiylx.weather.common.Route
import com.kiylx.weather.repo.QWeatherGeoRepo
import com.kiylx.weather.repo.bean.LocationEntity

@Composable
fun LocationManagerPage(navController: NavController) {
    val locationList =  QWeatherGeoRepo.allLocationState

    Box {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            item {
                Text(
                    text = "位置", modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.displayMedium
                )
            }
            items(locationList) {
                LocationItem(location = it)
            }
        }
        FloatIconTextButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    bottom = 32.dp,
                    end = 16.dp
                ),
            text = stringResource(R.string.add)
        ) {
            navController.navigate(Route.LOCATION_ADD_PAGE)
        }
    }
}

@Composable
fun LocationItem(location: LocationEntity) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.secondaryContainer,
                    RoundedCornerShape(8.dp)
                )
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 4.dp)
                    .size(36.dp).weight(1f),
                imageVector = Icons.Filled.LocationCity,
                contentDescription = null
            )
            Column(modifier = Modifier.padding(start = 4.dp).weight(5f)) {
                Text(
                    modifier = Modifier.padding(4.dp),
                    text = "${location.name}-${location.adm2}"
                )
                Text(
                    modifier = Modifier.padding(
                        start = 4.dp,
                        bottom = 4.dp,
                        end = 4.dp
                    ), text = "${location.adm1}-${location.country}"
                )
            }
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 4.dp)
                    .size(36.dp).weight(1f)
                    .clickable {
                        QWeatherGeoRepo.deleteLocation(location)
                    },
                imageVector = Icons.Filled.DeleteForever,
                contentDescription = null,
            )
        }
    }
}