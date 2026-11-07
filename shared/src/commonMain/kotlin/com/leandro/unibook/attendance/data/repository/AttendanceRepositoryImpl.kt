package com.leandro.unibook.attendance.data.repository

import com.leandro.unibook.attendance.data.remote.AttendanceApiService
import com.leandro.unibook.attendance.data.remote.dto.AttendanceRequestDto
import com.leandro.unibook.attendance.domain.model.AttendanceParams
import com.leandro.unibook.attendance.domain.model.AttendanceResult
import com.leandro.unibook.attendance.domain.repository.AttendanceRepository
import kotlinx.coroutines.CancellationException

class AttendanceRepositoryImpl(
    private val apiService: AttendanceApiService
) : AttendanceRepository {
    override suspend fun create(params: AttendanceParams): AttendanceResult {
        return try {
            apiService.create(
                AttendanceRequestDto(
                    nome = params.nome.trim(),
                    telefone = params.telefone.trim(),
                    situacaoNaIgreja = params.situacaoNaIgreja.map { it.name },
                    maiorProblema = params.maiorProblema.trim(),
                    dataAtendimento = params.dataAtendimento
                )
            )
            AttendanceResult.Success
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            AttendanceResult.Error(e.message ?: "Erro ao enviar o atendimento.")
        }
    }
}
