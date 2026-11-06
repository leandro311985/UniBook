package com.leandro.unibook.auth.domain.model

data class RegisterParams(
    val nome: String,
    val email: String,
    val senha: String
)
