package com.leandro.unibook.attendance.data.local

import kotlinx.serialization.Serializable

enum class SyncStatus {
    PENDING_SYNC,
    SYNCED
}

@Serializable
data class AttendanceEntity(
    val id: String,
    val nome: String,
    val endereco: String = "",
    val bairro: String = "",
    val telefone: String,
    val email: String = "",
    val situacaoNaIgreja: List<String> = emptyList(),
    val maiorProblema: String,
    val cultosAFrequentar: String = "",
    val responsavelAcompanhamento: String = "",
    val dataAtendimento: String,
    val vistoDoPastor: String = "Pendente",
    val fotoBase64: String = "",
    val syncStatus: SyncStatus = SyncStatus.PENDING_SYNC
)
