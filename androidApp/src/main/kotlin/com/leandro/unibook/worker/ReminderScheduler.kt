package com.leandro.unibook.worker

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

object ReminderScheduler {
    fun schedulePeriodicReminders(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
            15, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "pastoral_reminder_work",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
