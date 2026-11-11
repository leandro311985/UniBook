package com.leandro.unibook.attendance.domain.model

data class AttendanceParams(
    val nome: String,
    val endereco: String = "",
    val bairro: String = "",
    val telefone: String,
    val email: String = "",
    val situacaoNaIgreja: List<ChurchSituation>,
    val maiorProblema: String,
    val cultosAFrequentar: String = "",
    val responsavelAcompanhamento: String = "",
    val dataAtendimento: String,
    val vistoDoPastor: String = "Pendente",
    val fotoBase64: String = ""
)

enum class ChurchSituation {
    PRIMEIRA_VEZ,
    AFASTADO,
    MEMBRO
}
