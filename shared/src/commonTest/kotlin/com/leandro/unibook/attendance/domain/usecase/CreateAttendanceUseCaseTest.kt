package com.leandro.unibook.attendance.domain.usecase

import com.leandro.unibook.attendance.domain.model.AttendanceItem
import com.leandro.unibook.attendance.domain.model.AttendanceParams
import com.leandro.unibook.attendance.domain.model.AttendanceResult
import com.leandro.unibook.attendance.domain.model.ChurchSituation
import com.leandro.unibook.attendance.domain.repository.AttendanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CreateAttendanceUseCaseTest {
    private val repository = FakeAttendanceRepository()
    private val useCase = CreateAttendanceUseCase(repository)

    @Test
    fun validAttendanceIsSentToRepository() = runTest {
        val params = validParams()

        val result = useCase(params)

        assertEquals(AttendanceResult.Success, result)
        assertEquals(params, repository.receivedParams)
    }

    @Test
    fun blankNameIsRejectedWithoutCallingRepository() = runTest {
        val result = useCase(validParams().copy(nome = " "))

        assertEquals(AttendanceResult.Error("Informe o nome."), result)
        assertNull(repository.receivedParams)
    }

    @Test
    fun blankPhoneIsRejectedWithoutCallingRepository() = runTest {
        val result = useCase(validParams().copy(telefone = " "))

        assertEquals(AttendanceResult.Error("Informe o telefone."), result)
        assertNull(repository.receivedParams)
    }

    @Test
    fun invalidDateFormatIsRejectedWithoutCallingRepository() = runTest {
        val result = useCase(validParams().copy(dataAtendimento = "07/11/2026"))

        assertEquals(
            AttendanceResult.Error("Informe a data no formato AAAA-MM-DD."),
            result
        )
        assertNull(repository.receivedParams)
    }

    private fun validParams() = AttendanceParams(
        nome = "Maria Silva",
        telefone = "11999999999",
        situacaoNaIgreja = listOf(ChurchSituation.PRIMEIRA_VEZ),
        maiorProblema = "Está passando por dificuldades",
        dataAtendimento = "2026-11-07"
    )

    private class FakeAttendanceRepository : AttendanceRepository {
        var receivedParams: AttendanceParams? = null

        override suspend fun create(params: AttendanceParams): AttendanceResult {
            receivedParams = params
            return AttendanceResult.Success
        }

        override suspend fun update(id: String, params: AttendanceParams): AttendanceResult {
            receivedParams = params
            return AttendanceResult.Success
        }

        override suspend fun getAll(forceFetch: Boolean): List<AttendanceItem> {
            return emptyList()
        }

        override fun observeAll(): Flow<List<AttendanceItem>> {
            return flowOf(emptyList())
        }

        override suspend fun syncPending(): Int {
            return 0
        }

        override suspend fun confirmReminder(id: String) {
        }
    }
}
