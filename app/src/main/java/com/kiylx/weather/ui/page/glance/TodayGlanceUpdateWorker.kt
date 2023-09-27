package com.kiylx.weather.ui.page.glance

import android.content.Context
import androidx.glance.GlanceId
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.kiylx.weather.ui.page.main.DayWeatherType

class TodayGlanceUpdateWorker(
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        TodayGlanceRepo.run {
            // 需要执行的操作
            weatherHolder.getDailyData()
            weatherHolder.getDayWeatherData(DayWeatherType.threeDayWeather)
            weatherHolder.getDailyHourWeatherData()
        }

        return Result.success()
    }
    companion object {

        private val uniqueWorkName ="update_weather_kiylx"

        // 排队进行工作
        fun enqueue(context: Context, glanceId: GlanceId, force: Boolean = false) {
            val manager = WorkManager.getInstance(context)

            val requestBuilder = OneTimeWorkRequestBuilder<TodayGlanceUpdateWorker>().apply {
                addTag(glanceId.toString())
                setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                setInputData(
                    Data.Builder()
                        .putBoolean("force", force)
                        .build()
                )
            }
            val workPolicy = if (force) {
                ExistingWorkPolicy.REPLACE
            } else {
                ExistingWorkPolicy.KEEP
            }

            manager.enqueueUniqueWork(
                uniqueWorkName +glanceId,
                workPolicy,
                requestBuilder.build()
            )
        }

        /**
         * 取消任何正在进行的工作
         */
        fun cancel(context: Context, glanceId: GlanceId) {
            WorkManager.getInstance(context).cancelAllWorkByTag(glanceId.toString())
        }
    }
}