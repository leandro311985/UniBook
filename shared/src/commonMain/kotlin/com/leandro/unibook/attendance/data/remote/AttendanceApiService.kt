package com.leandro.unibook.attendance.data.remote

import com.leandro.unibook.attendance.data.remote.dto.AttendanceRequestDto
import com.leandro.unibook.attendance.data.remote.dto.AttendanceResponseDto
import com.leandro.unibook.core.device.DeviceIdProvider
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
    private val deviceIdProvider: DeviceIdProvider? = null,
    private val baseUrl: String = ATTENDANCE_API_BASE_URL
) {
    private fun resolveDeviceId(overrideId: String?): String {
        return overrideId.takeUnless { it.isNullOrBlank() }
            ?: deviceIdProvider?.getDeviceId()
            ?: "123e4567-e89b-12d3-a456-426614174000"
    }

    suspend fun create(
        request: AttendanceRequestDto,
        deviceId: String? = null
    ) {
        val activeDeviceId = resolveDeviceId(deviceId)
        client.post("$baseUrl/api/atendimentos") {
            contentType(ContentType.Application.Json)
            header("X-Device-Id", activeDeviceId)
            setBody(request)
        }
    }

    suspend fun update(
        id: String,
        request: AttendanceRequestDto,
        deviceId: String? = null
    ) {
        val activeDeviceId = resolveDeviceId(deviceId)
        client.put("$baseUrl/api/atendimentos/$id") {
            contentType(ContentType.Application.Json)
            header("X-Device-Id", activeDeviceId)
            setBody(request)
        }
    }

    suspend fun getAll(
        deviceId: String? = null
    ): List<AttendanceResponseDto> {
        val activeDeviceId = resolveDeviceId(deviceId)
        return client.get("$baseUrl/api/atendimentos") {
            header("X-Device-Id", activeDeviceId)
        }.body()
    }
}
