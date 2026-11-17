package com.leandro.unibook.attendance.data.local

import android.content.Context

class AndroidAttendanceStorageRepository(
    context: Context
) : AttendanceStorageRepository {

    private val prefs = context.getSharedPreferences("unibook_attendance_cache", Context.MODE_PRIVATE)

    override fun getSavedAttendancesJson(): String? {
        return prefs.getString("KEY_ATTENDANCE_ITEMS", null)
    }

    override fun saveAttendancesJson(json: String) {
        prefs.edit().putString("KEY_ATTENDANCE_ITEMS", json).apply()
    }
}
