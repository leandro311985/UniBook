package com.leandro.unibook.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.leandro.unibook.attendance.domain.repository.AttendanceRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val repository: AttendanceRepository by inject()

    override suspend fun doWork(): Result {
        return try {
            repository.syncPending()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
