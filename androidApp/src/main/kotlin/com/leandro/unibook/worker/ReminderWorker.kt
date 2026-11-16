package com.leandro.unibook.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.leandro.unibook.attendance.data.remote.AttendanceApiService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val apiService: AttendanceApiService by inject()

    override suspend fun doWork(): Result {
        return try {
            val reminders = apiService.getReminders()

            reminders.forEachIndexed { index, reminder ->
                val notificationId = reminder.atendimento.id?.hashCode() ?: (1000 + index)
                val workTag = "delayed_reminder_${notificationId}"

                val inputData = workDataOf(
                    "NOTIFICATION_ID" to notificationId,
                    "NOTIFICATION_TITLE" to "✝ Lembrete Pastoral: ${reminder.atendimento.nome}",
                    "NOTIFICATION_MESSAGE" to reminder.mensagem
                )

                // Agenda disparo com delay de 30 MINUTOS após receber da API
                val delayedWork = OneTimeWorkRequestBuilder<DelayedNotificationWorker>()
                    .setInitialDelay(30, TimeUnit.MINUTES)
                    .setInputData(inputData)
                    .build()

                WorkManager.getInstance(applicationContext).enqueueUniqueWork(
                    workTag,
                    ExistingWorkPolicy.KEEP,
                    delayedWork
                )
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
