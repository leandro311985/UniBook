package com.leandro.unibook.attendance.data.remote

import com.leandro.unibook.attendance.data.remote.dto.AttendanceRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.http.content.TextContent
import io.ktor.serialization.kotlinx.json.json
import io.ktor.client.request.HttpRequestData
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AttendanceApiServiceTest {
    @Test
    fun createPostsAttendanceJsonToApiEndpoint() = runTest {
        var capturedRequest: HttpRequestData? = null
        val client = HttpClient(
            MockEngine { request ->
                capturedRequest = request
                respond(
                    content = "",
                    status = HttpStatusCode.Created,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                )
            }
        ) {
            install(ContentNegotiation) {
                json()
            }
        }
        val request = AttendanceRequestDto(
            nome = "Maria Silva",
            telefone = "11999999999",
            situacaoNaIgreja = listOf("PRIMEIRA_VEZ"),
            maiorProblema = "Está passando por dificuldades",
            dataAtendimento = "2026-11-07",
            fotoBase64 = "dGVzdA==",
            foto = "dGVzdA=="
        )

        try {
            AttendanceApiService(client = client, baseUrl = "http://localhost:8082").create(request)
        } finally {
            client.close()
        }

        val sentRequest = assertNotNull(capturedRequest)
        assertEquals(HttpMethod.Post, sentRequest.method)
        assertEquals("/api/atendimentos", sentRequest.url.encodedPath)
        assertEquals("123e4567-e89b-12d3-a456-426614174000", sentRequest.headers["X-Device-Id"])
        val sentBody = sentRequest.body as TextContent
        assertEquals(
            ContentType.Application.Json.toString(),
            sentBody.contentType.toString()
        )
        assertEquals(
            """{"nome":"Maria Silva","endereco":null,"bairro":null,"telefone":"11999999999","email":null,"situacaoNaIgreja":["PRIMEIRA_VEZ"],"maiorProblema":"Está passando por dificuldades","cultosAFrequentar":null,"responsavelAcompanhamento":null,"dataAtendimento":"2026-11-07","vistoDoPastor":"Pendente","fotoBase64":"dGVzdA==","foto":"dGVzdA==","nomeDoGrupo":null}""",
            sentBody.text
        )
    }

    @Test
    fun updatePutsAttendanceToIdEndpoint() = runTest {
        var capturedRequest: HttpRequestData? = null
        val client = HttpClient(
            MockEngine { request ->
                capturedRequest = request
                respond(
                    content = "",
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                )
            }
        ) {
            install(ContentNegotiation) {
                json()
            }
        }

        try {
            AttendanceApiService(client = client, baseUrl = "http://localhost:8082").update(
                id = "attendance-123",
                request = AttendanceRequestDto(
                    nome = "Maria Silva",
                    telefone = "11999999999",
                    situacaoNaIgreja = listOf("MEMBRO"),
                    maiorProblema = "Acompanhamento",
                    dataAtendimento = "2026-11-07"
                )
            )
        } finally {
            client.close()
        }

        val sentRequest = assertNotNull(capturedRequest)
        assertEquals(HttpMethod.Put, sentRequest.method)
        assertEquals("/api/atendimentos/attendance-123", sentRequest.url.encodedPath)
        assertEquals("123e4567-e89b-12d3-a456-426614174000", sentRequest.headers["X-Device-Id"])
    }

    @Test
    fun deleteRemovesAttendanceFromIdEndpoint() = runTest {
        var capturedRequest: HttpRequestData? = null
        val client = HttpClient(
            MockEngine { request ->
                capturedRequest = request
                respond(content = "", status = HttpStatusCode.NoContent)
            }
        )

        try {
            AttendanceApiService(client = client, baseUrl = "http://localhost:8082")
                .delete(id = "attendance-123")
        } finally {
            client.close()
        }

        val sentRequest = assertNotNull(capturedRequest)
        assertEquals(HttpMethod.Delete, sentRequest.method)
        assertEquals("/api/atendimentos/attendance-123", sentRequest.url.encodedPath)
        assertEquals("123e4567-e89b-12d3-a456-426614174000", sentRequest.headers["X-Device-Id"])
    }

    @Test
    fun confirmReminderPostsToConcluirEndpoint() = runTest {
        var capturedRequest: HttpRequestData? = null
        val client = HttpClient(
            MockEngine { request ->
                capturedRequest = request
                respond(
                    content = "",
                    status = HttpStatusCode.NoContent
                )
            }
        )

        try {
            AttendanceApiService(client = client, baseUrl = "http://localhost:8082").confirmReminder(
                id = "6afc6a945c1f29f249606484"
            )
        } finally {
            client.close()
        }

        val sentRequest = assertNotNull(capturedRequest)
        assertEquals(HttpMethod.Post, sentRequest.method)
        assertEquals("/api/atendimentos/6afc6a945c1f29f249606484/lembrete/concluir", sentRequest.url.encodedPath)
        assertEquals("123e4567-e89b-12d3-a456-426614174000", sentRequest.headers["X-Device-Id"])
    }
}
