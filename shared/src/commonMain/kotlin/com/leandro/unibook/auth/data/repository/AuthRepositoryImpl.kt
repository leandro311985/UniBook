package com.leandro.unibook.auth.data.repository

import com.leandro.unibook.auth.data.remote.AuthApiService
import com.leandro.unibook.auth.data.local.TokenStorage
import com.leandro.unibook.auth.data.remote.dto.LoginRequestDto
import com.leandro.unibook.auth.data.remote.dto.RegisterRequestDto
import com.leandro.unibook.auth.domain.model.AuthResult
import com.leandro.unibook.auth.domain.model.LoginParams
import com.leandro.unibook.auth.domain.model.LoginSession
import com.leandro.unibook.auth.domain.model.RegisterParams
import com.leandro.unibook.auth.domain.repository.AuthRepository
import kotlinx.coroutines.CancellationException

class AuthRepositoryImpl(
    private val apiService: AuthApiService,
    private val tokenStorage: TokenStorage
) : AuthRepository {
    override suspend fun register(params: RegisterParams): AuthResult<String> {
        return try {
            val dto = RegisterRequestDto(
                nome = params.nome,
                email = params.email,
                senha = params.senha
            )
            apiService.registrar(dto)
            AuthResult.Success(data = "Cadastro realizado com sucesso!")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Erro ao conectar com o servidor.")
        }
    }

    override suspend fun login(params: LoginParams): AuthResult<LoginSession> {
        return try {
            val response = apiService.login(
                LoginRequestDto(
                    email = params.email,
                    senha = params.senha
                )
            )
            tokenStorage.saveToken(response.accessToken)
            AuthResult.Success(
                LoginSession(
                    accessToken = response.accessToken,
                    userId = response.id?.toString()?.removeSurrounding("\""),
                    expiresAt = response.expiresAt
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Erro ao conectar com o servidor.")
        }
    }
}
