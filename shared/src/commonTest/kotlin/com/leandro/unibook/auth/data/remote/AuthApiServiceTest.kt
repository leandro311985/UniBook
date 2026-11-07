package com.leandro.unibook.auth.data.remote

import com.leandro.unibook.auth.data.local.TokenStorage
import com.leandro.unibook.auth.data.remote.dto.RegisterRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AuthApiServiceTest {
    @Test
    fun registerPostsCredentialsToRegistrationEndpoint() = runTest {
        var capturedRequest: HttpRequestData? = null
        val client = HttpClient(
            MockEngine { request ->
                capturedRequest = request
                respond(
                    content = "",
                    status = HttpStatusCode.Created,
                    headers = headersOf(
                        HttpHeaders.ContentType,
                        ContentType.Application.Json.toString()
                    )
                )
            }
        ) {
            install(ContentNegotiation) {
                json()
            }
        }
        val service = AuthApiService(
            baseUrl = "http://localhost:8082",
            tokenStorage = FakeTokenStorage(),
            loginClient = client
        )

        try {
            service.registrar(
                RegisterRequestDto(
                    nome = "Maria Silva",
                    email = "maria@example.com",
                    senha = "senha123"
                )
            )
        } finally {
            client.close()
        }

        val sentRequest = assertNotNull(capturedRequest)
        assertEquals(HttpMethod.Post, sentRequest.method)
        assertEquals("/api/auth/registrar", sentRequest.url.encodedPath)
        val sentBody = sentRequest.body as TextContent
        assertEquals(ContentType.Application.Json, sentBody.contentType)
        assertEquals(
            """{"nome":"Maria Silva","email":"maria@example.com","senha":"senha123"}""",
            sentBody.text
        )
    }

    private class FakeTokenStorage : TokenStorage {
        override fun getToken(): String? = null
        override fun saveToken(token: String) = Unit
        override fun clearToken() = Unit
    }
}
