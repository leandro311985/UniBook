package com.leandro.unibook.attendance.domain.repository

import com.leandro.unibook.attendance.domain.model.AttendanceParams
import com.leandro.unibook.attendance.domain.model.AttendanceResult

interface AttendanceRepository {
    suspend fun create(params: AttendanceParams): AttendanceResult
}
