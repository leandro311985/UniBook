package com.leandro.unibook.auth.data.remote

import com.leandro.unibook.auth.data.local.TokenStorage
import com.leandro.unibook.auth.data.remote.dto.LoginRequestDto
import com.leandro.unibook.auth.data.remote.dto.LoginResponseDto
import com.leandro.unibook.auth.data.remote.dto.RegisterRequestDto
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class AuthApiService(
    private val baseUrl: String = "http://10.0.2.2:8082",
    tokenStorage: TokenStorage,
    private val client: HttpClient = createDefaultHttpClient(tokenStorage),
    private val loginClient: HttpClient = createDefaultHttpClient()
) {
    suspend fun registrar(request: RegisterRequestDto) {
        loginClient.post("$baseUrl/api/auth/registrar") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    suspend fun login(request: LoginRequestDto): LoginResponseDto {
        return loginClient.post("$baseUrl/api/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    companion object {
        fun createDefaultHttpClient(tokenStorage: TokenStorage? = null): HttpClient {
            return HttpClient {
                expectSuccess = true
                if (tokenStorage != null) {
                    install(Auth) {
                        bearer {
                            loadTokens {
                                tokenStorage.getToken()?.let { token ->
                                    BearerTokens(accessToken = token, refreshToken = "")
                                }
                            }
                            sendWithoutRequest { true }
                        }
                    }
                }
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    })
                }
            }
        }
    }
}
