package com.leandro.unibook.attendance.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AttendanceRequestDto(
    val nome: String,
    val telefone: String,
    val situacaoNaIgreja: List<String>,
    val maiorProblema: String,
    val dataAtendimento: String
)
