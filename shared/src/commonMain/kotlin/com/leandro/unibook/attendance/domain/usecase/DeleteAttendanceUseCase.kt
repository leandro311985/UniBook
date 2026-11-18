package com.leandro.unibook.attendance.domain.usecase

import com.leandro.unibook.attendance.domain.model.AttendanceResult
import com.leandro.unibook.attendance.domain.repository.AttendanceRepository

class DeleteAttendanceUseCase(
    private val repository: AttendanceRepository
) {
    suspend operator fun invoke(id: String): AttendanceResult {
        return repository.delete(id)
    }
}
