package com.leandro.unibook.home.data.remote

import com.leandro.unibook.attendance.data.remote.ATTENDANCE_API_BASE_URL
import com.leandro.unibook.core.device.DeviceIdProvider
import com.leandro.unibook.home.data.remote.dto.UserDataRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class UserApiService(
    private val client: HttpClient,
    private val deviceIdProvider: DeviceIdProvider? = null,
    private val baseUrl: String = ATTENDANCE_API_BASE_URL
) {
    private fun resolveDeviceId(overrideId: String? = null): String {
        return overrideId.takeUnless { it.isNullOrBlank() }
            ?: deviceIdProvider?.getDeviceId()
            ?: "123e4567-e89b-12d3-a456-426614174000"
    }

    suspend fun saveUserData(
        nickname: String,
        telefone: String,
        deviceId: String? = null
    ) {
        val activeDeviceId = resolveDeviceId(deviceId)
        client.post("$baseUrl/api/user-data") {
            contentType(ContentType.Application.Json)
            header("X-Device-Id", activeDeviceId)
            setBody(UserDataRequestDto(nickname = nickname, telefone = telefone))
        }
    }
}
