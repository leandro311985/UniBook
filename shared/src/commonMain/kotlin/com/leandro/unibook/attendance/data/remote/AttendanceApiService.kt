package com.leandro.unibook.attendance.data.remote

import com.leandro.unibook.attendance.data.remote.dto.AttendanceRequestDto
import com.leandro.unibook.auth.data.remote.API_BASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AttendanceApiService(
    private val client: HttpClient,
    private val baseUrl: String = API_BASE_URL
) {
    suspend fun create(request: AttendanceRequestDto) {
        client.post("$baseUrl/api/atendimentos") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }
}
