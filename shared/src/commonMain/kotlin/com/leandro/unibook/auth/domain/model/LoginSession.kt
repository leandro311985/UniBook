package com.leandro.unibook.auth.domain.model

data class LoginSession(
    val accessToken: String,
    val userId: String?,
    val expiresAt: String
)
