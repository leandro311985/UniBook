package com.leandro.unibook.attendance.domain.model

private val storageDatePattern = Regex("""(\d{4})-(\d{2})-(\d{2})""")
private val displayDatePattern = Regex("""(\d{2})/(\d{2})/(\d{4})""")

fun String.toAttendanceDisplayDate(): String {
    val match = storageDatePattern.matchEntire(this) ?: return this
    return "${match.groupValues[3]}/${match.groupValues[2]}/${match.groupValues[1]}"
}

fun String.toAttendanceStorageDate(): String {
    val match = displayDatePattern.matchEntire(this) ?: return this
    return "${match.groupValues[3]}-${match.groupValues[2]}-${match.groupValues[1]}"
}
