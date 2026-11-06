package com.leandro.unibook.auth.data.remote.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonPrimitive

@Serializable
data class LoginResponseDto(
    val accessToken: String,
    val id: JsonPrimitive? = null,
    val expiresAt: String
)
