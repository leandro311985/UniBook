package com.leandro.unibook.attendance.data.local

import platform.Foundation.NSUserDefaults

class IosAttendanceStorageRepository : AttendanceStorageRepository {

    private val defaults = NSUserDefaults.standardUserDefaults

    override fun getSavedAttendancesJson(): String? {
        return defaults.stringForKey("KEY_ATTENDANCE_ITEMS")
    }

    override fun saveAttendancesJson(json: String) {
        defaults.setObject(json, "KEY_ATTENDANCE_ITEMS")
    }
}
