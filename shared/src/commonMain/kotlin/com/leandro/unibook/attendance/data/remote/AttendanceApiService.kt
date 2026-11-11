package com.leandro.unibook.attendance.data.remote

import com.leandro.unibook.attendance.data.remote.dto.AttendanceRequestDto
import com.leandro.unibook.attendance.data.remote.dto.AttendanceResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AttendanceApiService(
    private val client: HttpClient,
    private val baseUrl: String = ATTENDANCE_API_BASE_URL
) {
    suspend fun create(
        request: AttendanceRequestDto,
        deviceId: String = "123e4567-e89b-12d3-a456-426614174000"
    ) {
        client.post("$baseUrl/api/atendimentos") {
            contentType(ContentType.Application.Json)
            header("X-Device-Id", deviceId)
            setBody(request)
        }
    }

    suspend fun update(
        id: String,
        request: AttendanceRequestDto,
        deviceId: String = "123e4567-e89b-12d3-a456-426614174000"
    ) {
        client.put("$baseUrl/api/atendimentos/$id") {
            contentType(ContentType.Application.Json)
            header("X-Device-Id", deviceId)
            setBody(request)
        }
    }

    suspend fun getAll(
        deviceId: String = "123e4567-e89b-12d3-a456-426614174000"
    ): List<AttendanceResponseDto> {
        return client.get("$baseUrl/api/atendimentos") {
            header("X-Device-Id", deviceId)
        }.body()
    }
}
