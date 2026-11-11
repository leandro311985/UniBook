package com.leandro.unibook.attendance.domain.model

data class AttendanceItem(
    val id: String,
    val nome: String,
    val endereco: String,
    val bairro: String,
    val telefone: String,
    val email: String,
    val situacaoNaIgreja: List<String>,
    val maiorProblema: String,
    val cultosAFrequentar: String,
    val responsavelAcompanhamento: String,
    val dataAtendimento: String,
    val vistoDoPastor: String,
    val fotoBase64: String = ""
)
