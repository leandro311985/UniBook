package com.leandro.unibook.attendance.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class AttendanceDateFormatTest {
    @Test
    fun convertsStorageDateToDisplayFormat() {
        assertEquals("07/11/2026", "2026-11-07".toAttendanceDisplayDate())
    }

    @Test
    fun convertsDisplayDateToStorageFormat() {
        assertEquals("2026-11-07", "07/11/2026".toAttendanceStorageDate())
    }

    @Test
    fun leavesUnrecognizedDateFormatsUnchanged() {
        assertEquals("7/11/2026", "7/11/2026".toAttendanceStorageDate())
        assertEquals("not a date", "not a date".toAttendanceDisplayDate())
    }
}
