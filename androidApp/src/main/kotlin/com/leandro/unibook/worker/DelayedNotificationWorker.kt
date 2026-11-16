package com.leandro.unibook.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.leandro.unibook.notification.NotificationHelper

class DelayedNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val id = inputData.getInt("NOTIFICATION_ID", 1001)
        val title = inputData.getString("NOTIFICATION_TITLE") ?: "✝ Lembrete Pastoral"
        val message = inputData.getString("NOTIFICATION_MESSAGE") ?: "Você tem um acompanhamento pendente."

        NotificationHelper.showReminderNotification(
            context = applicationContext,
            id = id,
            title = title,
            message = message
        )

        return Result.success()
    }
}
