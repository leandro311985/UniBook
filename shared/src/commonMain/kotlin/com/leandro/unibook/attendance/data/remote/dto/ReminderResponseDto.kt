package com.leandro.unibook.attendance.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ReminderResponseDto(
    val atendimento: AttendanceResponseDto,
    val mensagem: String
)
