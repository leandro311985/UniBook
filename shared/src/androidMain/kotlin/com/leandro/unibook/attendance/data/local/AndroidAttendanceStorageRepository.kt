package com.leandro.unibook.attendance.data.local

import android.content.Context
import com.leandro.unibook.core.security.SecurePreferences

class AndroidAttendanceStorageRepository(
    context: Context
) : AttendanceStorageRepository {

    private val prefs = SecurePreferences(context, "unibook_attendance_cache")

    override fun getSavedAttendancesJson(): String? {
        return prefs.getString(KEY_ATTENDANCE_ITEMS)
    }

    override fun saveAttendancesJson(json: String) {
        prefs.putString(KEY_ATTENDANCE_ITEMS, json)
    }

    private companion object {
        const val KEY_ATTENDANCE_ITEMS = "KEY_ATTENDANCE_ITEMS"
    }
}
