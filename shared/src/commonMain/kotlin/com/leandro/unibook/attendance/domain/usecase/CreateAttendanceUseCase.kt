package com.leandro.unibook.attendance.domain.usecase

import com.leandro.unibook.attendance.domain.model.AttendanceParams
import com.leandro.unibook.attendance.domain.model.AttendanceResult
import com.leandro.unibook.attendance.domain.repository.AttendanceRepository

class CreateAttendanceUseCase(
    private val repository: AttendanceRepository
) {
    suspend operator fun invoke(params: AttendanceParams): AttendanceResult {
        if (params.nome.isBlank()) {
            return AttendanceResult.Error("Informe o nome.")
        }
        if (params.telefone.isBlank()) {
            return AttendanceResult.Error("Informe o telefone.")
        }
        if (!params.dataAtendimento.matches(Regex("""\d{4}-\d{2}-\d{2}"""))) {
            return AttendanceResult.Error("Informe a data no formato DD/MM/AAAA.")
        }
        return repository.create(params)
    }
}
