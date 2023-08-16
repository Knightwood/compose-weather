package com.kiylx.weather.common

import com.kiylx.weather.repo.bean.DailyEntity
import java.time.LocalDateTime
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit

/**
 * 判断数据是否已经过时
 */
class DataCacheUtil {
    companion object {
        fun checkIsOutOfDate(dailyEntity: DailyEntity?): Boolean {
            if (dailyEntity == null) {
                return true
            }
            val outOfData: Boolean
            val mins = AllPrefs.dailyInterval
            val now = LocalDateTime.now()
            val up = LocalDateTime.parse(dailyEntity.updateTime)
            outOfData = (up.isBefore(now) && up.until(now, ChronoUnit.MINUTES) >= mins)
            return outOfData
        }
    }
}