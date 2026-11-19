package com.leandro.unibook.attendance.domain.repository

import com.leandro.unibook.attendance.domain.model.AttendanceItem
import com.leandro.unibook.attendance.domain.model.AttendanceParams
import com.leandro.unibook.attendance.domain.model.AttendanceResult
import kotlinx.coroutines.flow.Flow

interface AttendanceRepository {
    suspend fun create(params: AttendanceParams): AttendanceResult
    suspend fun update(id: String, params: AttendanceParams): AttendanceResult
    suspend fun delete(id: String): AttendanceResult
    suspend fun getAll(forceFetch: Boolean = false): List<AttendanceItem>
    fun observeAll(): Flow<List<AttendanceItem>>
    suspend fun syncPending(): Int
    suspend fun confirmReminder(id: String)
}
