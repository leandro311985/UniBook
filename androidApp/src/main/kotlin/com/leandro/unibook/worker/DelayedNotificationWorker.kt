package com.leandro.unibook.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.leandro.unibook.attendance.data.remote.AttendanceApiService
import com.leandro.unibook.notification.NotificationHelper
import kotlinx.coroutines.CancellationException
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DelayedNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val apiService: AttendanceApiService by inject()

    override suspend fun doWork(): Result {
        val attendanceId = inputData.getString("ATTENDANCE_ID")
        val id = inputData.getInt("NOTIFICATION_ID", 1001)
        val title = inputData.getString("NOTIFICATION_TITLE") ?: "✝ Lembrete Pastoral"
        val message = inputData.getString("NOTIFICATION_MESSAGE") ?: "Você tem um acompanhamento pendente."

        val notificationShown = NotificationHelper.showReminderNotification(
            context = applicationContext,
            id = id,
            title = title,
            message = message
        )
        if (!notificationShown) {
            return Result.retry()
        }

        if (!attendanceId.isNullOrBlank()) {
            try {
                apiService.confirmReminder(attendanceId)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                return Result.retry()
            }
        }

        return Result.success()
    }
}
