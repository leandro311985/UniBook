package com.leandro.unibook.attendance.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AttendanceResponseDto(
    val id: String? = null,
    val nome: String = "",
    val endereco: String? = null,
    val bairro: String? = null,
    val telefone: String = "",
    val email: String? = null,
    val situacaoNaIgreja: List<String> = emptyList(),
    val maiorProblema: String = "",
    val cultosAFrequentar: String? = null,
    val responsavelAcompanhamento: String? = null,
    val dataAtendimento: String = "",
    val vistoDoPastor: String? = "Pendente",
    val fotoBase64: String? = null,
    val foto: String? = null,
    val fotoUrl: String? = null,
    val nomeDoGrupo: String? = null
) {
    val effectiveFotoBase64: String
        get() = fotoBase64 ?: foto ?: fotoUrl ?: ""
}
