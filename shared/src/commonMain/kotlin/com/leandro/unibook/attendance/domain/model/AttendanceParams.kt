package com.leandro.unibook.attendance.domain.model

data class AttendanceParams(
    val nome: String,
    val telefone: String,
    val situacaoNaIgreja: List<ChurchSituation>,
    val maiorProblema: String,
    val dataAtendimento: String
)

enum class ChurchSituation {
    PRIMEIRA_VEZ,
    AFASTADO,
    MEMBRO
}
