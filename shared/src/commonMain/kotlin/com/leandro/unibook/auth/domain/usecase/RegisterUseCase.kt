package com.leandro.unibook.auth.domain.usecase

import com.leandro.unibook.auth.domain.model.AuthResult
import com.leandro.unibook.auth.domain.model.RegisterParams
import com.leandro.unibook.auth.domain.repository.AuthRepository

class RegisterUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(params: RegisterParams): AuthResult<String> {
        if (params.nome.isBlank()) {
            return AuthResult.Error("O nome não pode estar em branco.")
        }
        if (params.email.isBlank() || !params.email.contains("@")) {
            return AuthResult.Error("E-mail inválido.")
        }
        if (params.senha.length < 6) {
            return AuthResult.Error("A senha deve ter pelo menos 6 caracteres.")
        }
        return repository.register(params)
    }
}
