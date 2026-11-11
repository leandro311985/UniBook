package com.leandro.unibook.attendance.domain.usecase

import com.leandro.unibook.attendance.domain.model.AttendanceItem
import com.leandro.unibook.attendance.domain.repository.AttendanceRepository
import kotlinx.coroutines.flow.Flow

class GetAttendancesUseCase(
    private val repository: AttendanceRepository
) {
    suspend operator fun invoke(): List<AttendanceItem> {
        return repository.getAll()
    }

    fun observe(): Flow<List<AttendanceItem>> {
        return repository.observeAll()
    }
}
