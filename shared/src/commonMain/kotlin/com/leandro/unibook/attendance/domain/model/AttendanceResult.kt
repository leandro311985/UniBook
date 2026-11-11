package com.leandro.unibook.attendance.domain.model

sealed interface AttendanceResult {
    data object Success : AttendanceResult
    data class Error(val message: String) : AttendanceResult
}
