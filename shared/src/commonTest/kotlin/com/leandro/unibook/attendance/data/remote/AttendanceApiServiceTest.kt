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
            dataAtendimento = "2026-11-07"
        )

        try {
            AttendanceApiService(client, "http://localhost:8082").create(request)
        } finally {
            client.close()
        }

        val sentRequest = assertNotNull(capturedRequest)
        assertEquals(HttpMethod.Post, sentRequest.method)
        assertEquals("/api/atendimentos", sentRequest.url.encodedPath)
        val sentBody = sentRequest.body as TextContent
        assertEquals(
            ContentType.Application.Json.toString(),
            sentBody.contentType.toString()
        )
        assertEquals(
            """{"nome":"Maria Silva","telefone":"11999999999","situacaoNaIgreja":["PRIMEIRA_VEZ"],"maiorProblema":"Está passando por dificuldades","dataAtendimento":"2026-11-07"}""",
            sentBody.text
        )
    }
}
