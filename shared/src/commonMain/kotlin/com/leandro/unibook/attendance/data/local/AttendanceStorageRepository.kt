package com.leandro.unibook.attendance.data.local

interface AttendanceStorageRepository {
    fun getSavedAttendancesJson(): String?
    fun saveAttendancesJson(json: String)
}
