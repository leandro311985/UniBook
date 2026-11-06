package com.leandro.unibook.auth.domain.repository

import com.leandro.unibook.auth.domain.model.AuthResult
import com.leandro.unibook.auth.domain.model.LoginParams
import com.leandro.unibook.auth.domain.model.LoginSession
import com.leandro.unibook.auth.domain.model.RegisterParams

interface AuthRepository {
    suspend fun register(params: RegisterParams): AuthResult<String>
    suspend fun login(params: LoginParams): AuthResult<LoginSession>
}
