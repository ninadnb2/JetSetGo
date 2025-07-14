package com.example.jetsetgo.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.jetsetgo.R

class StepReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        Log.d("StepReminderWorker", "Notification Worker triggered!")
        sendNotification()
        return Result.success()
    }

    private fun sendNotification() {
        Log.d("StepReminderWorker", "send notification")
        val channelId = "step_reminder_channel"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val soundUri = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI

        val channel = NotificationChannel(
            channelId,
            "Step Reminder",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            setSound(soundUri, null)
        }
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Keep Moving! 🏃")
            .setContentText("Don't forget to complete your step goal today.")
            .setSmallIcon(R.drawable.activity_tracker)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(soundUri)
            .build()

        notificationManager.notify(1, notification)
    }
}
