package com.leandro.unibook.auth.domain.usecase

import com.leandro.unibook.auth.domain.model.AuthResult
import com.leandro.unibook.auth.domain.model.LoginParams
import com.leandro.unibook.auth.domain.model.LoginSession
import com.leandro.unibook.auth.domain.repository.AuthRepository

class LoginUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(params: LoginParams): AuthResult<LoginSession> {
        if (params.email.isBlank() || !params.email.contains("@")) {
            return AuthResult.Error("E-mail inválido.")
        }
        if (params.senha.isBlank()) {
            return AuthResult.Error("A senha não pode estar em branco.")
        }
        return repository.login(params)
    }
}
