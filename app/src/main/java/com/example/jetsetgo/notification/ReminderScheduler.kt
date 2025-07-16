package com.example.jetsetgo.notification
import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

object ReminderScheduler {

    fun scheduleDailyReminder(context: Context) {

        val workRequest = PeriodicWorkRequestBuilder<StepReminderWorker>(3, TimeUnit.HOURS)
            .setInitialDelay(3, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "daily_step_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }


    fun scheduleTestReminder(context: Context) {
        val request = OneTimeWorkRequestBuilder<StepReminderWorker>()
            .setInitialDelay(5, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(context).enqueue(request)
    }

}
