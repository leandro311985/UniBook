package com.leandro.unibook.home.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDataRequestDto(
    val nickname: String,
    val telefone: String
)
