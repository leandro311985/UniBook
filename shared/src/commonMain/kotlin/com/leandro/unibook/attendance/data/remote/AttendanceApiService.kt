package com.leandro.unibook.attendance.data.remote

import com.leandro.unibook.attendance.data.remote.dto.AttendanceRequestDto
import com.leandro.unibook.attendance.data.remote.dto.AttendanceResponseDto
import com.leandro.unibook.attendance.data.remote.dto.ReminderResponseDto
import com.leandro.unibook.core.device.DeviceIdProvider
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.delete
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
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

    suspend fun createMultipart(
        request: AttendanceRequestDto,
        fotoBytes: ByteArray? = null,
        deviceId: String? = null
    ) {
        val activeDeviceId = resolveDeviceId(deviceId)
        client.submitFormWithBinaryData(
            url = "$baseUrl/api/atendimentos",
            formData = formData {
                append("nome", request.nome)
                request.endereco?.let { append("endereco", it) }
                request.bairro?.let { append("bairro", it) }
                append("telefone", request.telefone)
                request.email?.let { append("email", it) }
                request.situacaoNaIgreja.forEach { append("situacaoNaIgreja", it) }
                append("maiorProblema", request.maiorProblema)
                request.cultosAFrequentar?.let { append("cultosAFrequentar", it) }
                request.responsavelAcompanhamento?.let { append("responsavelAcompanhamento", it) }
                append("dataAtendimento", request.dataAtendimento)
                request.vistoDoPastor?.let { append("vistoDoPastor", it) }

                request.fotoBase64?.let {
                    append("fotoBase64", it)
                    append("foto", it)
                }

                if (fotoBytes != null && fotoBytes.isNotEmpty()) {
                    append("foto", fotoBytes, Headers.build {
                        append(HttpHeaders.ContentType, "image/jpeg")
                        append(HttpHeaders.ContentDisposition, "filename=\"foto.jpg\"")
                    })
                }
            }
        ) {
            header("X-Device-Id", activeDeviceId)
        }
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

    suspend fun delete(
        id: String,
        deviceId: String? = null
    ) {
        val activeDeviceId = resolveDeviceId(deviceId)
        client.delete("$baseUrl/api/atendimentos/$id") {
            header("X-Device-Id", activeDeviceId)
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

    suspend fun getReminders(
        deviceId: String? = null
    ): List<ReminderResponseDto> {
        val activeDeviceId = resolveDeviceId(deviceId)
        return client.get("$baseUrl/api/atendimentos/lembretes") {
            header("X-Device-Id", activeDeviceId)
        }.body()
    }

    suspend fun confirmReminder(
        id: String,
        deviceId: String? = null
    ) {
        val activeDeviceId = resolveDeviceId(deviceId)
        client.post("$baseUrl/api/atendimentos/$id/lembrete/concluir") {
            header("X-Device-Id", activeDeviceId)
        }
    }
}
