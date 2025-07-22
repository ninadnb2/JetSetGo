package com.example.jetsetgo.worker

import android.content.Context
import androidx.work.*
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

object MidnightResetScheduler {

    fun scheduleMidnightReset(context: Context) {
        val delay = calculateDelayToMidnight()

        val workRequest = PeriodicWorkRequestBuilder<MidnightResetWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "midnight_reset_work",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    private fun calculateDelayToMidnight(): Long {
        val now = LocalDateTime.now()
        val midnight = now.toLocalDate().plusDays(1).atTime(LocalTime.MIDNIGHT)
        return java.time.Duration.between(now, midnight).toMillis()
    }
}
