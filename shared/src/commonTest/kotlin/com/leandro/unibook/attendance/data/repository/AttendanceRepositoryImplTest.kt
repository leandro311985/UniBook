package com.leandro.unibook.attendance.data.repository

import com.leandro.unibook.attendance.data.local.AttendanceEntity
import com.leandro.unibook.attendance.data.local.AttendanceLocalDataSource
import com.leandro.unibook.attendance.data.local.PendingOperation
import com.leandro.unibook.attendance.data.local.SyncStatus
import com.leandro.unibook.attendance.data.remote.AttendanceApiService
import com.leandro.unibook.attendance.domain.model.AttendanceParams
import com.leandro.unibook.attendance.domain.model.AttendanceResult
import com.leandro.unibook.attendance.domain.model.ChurchSituation
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpMethod
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import io.ktor.serialization.kotlinx.json.json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class AttendanceRepositoryImplTest {
    @Test
    fun createReturnsWithItemSavedBeforeServerResponds() = runTest {
        val responseGate = CompletableDeferred<Unit>()
        val requestStarted = CompletableDeferred<Unit>()
        var capturedRequest: HttpRequestData? = null
        val client = HttpClient(
            MockEngine { request ->
                capturedRequest = request
                requestStarted.complete(Unit)
                responseGate.await()
                respond(
                    content = "",
                    status = HttpStatusCode.Created,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
        ) {
            install(ContentNegotiation) { json() }
        }
        val localDataSource = AttendanceLocalDataSource()
        val repository = AttendanceRepositoryImpl(
            apiService = AttendanceApiService(client, baseUrl = "http://localhost"),
            localDataSource = localDataSource,
            syncScope = this
        )

        try {
            assertEquals(
                AttendanceResult.Success,
                repository.create(validParams())
            )

            val saved = localDataSource.getPendingSync().single()
            assertEquals(SyncStatus.PENDING_SYNC, saved.syncStatus)

            runCurrent()
            requestStarted.await()
            assertEquals(SyncStatus.PENDING_SYNC, localDataSource.getById(saved.id)?.syncStatus)

            localDataSource.update(saved.copy(nome = "Maria Atualizada"))
            responseGate.complete(Unit)
            advanceUntilIdle()
            assertEquals(HttpMethod.Post, assertNotNull(capturedRequest).method)
            assertEquals("Maria Atualizada", localDataSource.getById(saved.id)?.nome)
            assertEquals(SyncStatus.PENDING_SYNC, localDataSource.getById(saved.id)?.syncStatus)
        } finally {
            client.close()
        }
    }

    @Test
    fun updateIsSavedAndQueuedBeforeServerResponds() = runTest {
        val responseGate = CompletableDeferred<Unit>()
        val requestStarted = CompletableDeferred<Unit>()
        var capturedRequest: HttpRequestData? = null
        val client = HttpClient(
            MockEngine { request ->
                capturedRequest = request
                requestStarted.complete(Unit)
                responseGate.await()
                respond(
                    content = "",
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
        ) {
            install(ContentNegotiation) { json() }
        }
        val localDataSource = AttendanceLocalDataSource()
        localDataSource.save(
            AttendanceEntity(
                id = "42",
                nome = "Maria Silva",
                telefone = "11999999999",
                maiorProblema = "Anterior",
                dataAtendimento = "2026-11-07",
                syncStatus = SyncStatus.SYNCED
            )
        )
        val repository = AttendanceRepositoryImpl(
            apiService = AttendanceApiService(client, baseUrl = "http://localhost"),
            localDataSource = localDataSource,
            syncScope = this
        )

        try {
            assertEquals(
                AttendanceResult.Success,
                repository.update("42", validParams())
            )

            val saved = localDataSource.getById("42")
            assertEquals(SyncStatus.PENDING_SYNC, saved?.syncStatus)
            assertEquals(PendingOperation.UPDATE, saved?.pendingOperation)

            runCurrent()
            requestStarted.await()
            assertEquals("2026-11-07", localDataSource.getById("42")?.dataAtendimento)
            assertEquals(SyncStatus.PENDING_SYNC, localDataSource.getById("42")?.syncStatus)

            responseGate.complete(Unit)
            advanceUntilIdle()
            val request = assertNotNull(capturedRequest)
            assertEquals(HttpMethod.Put, request.method)
            assertEquals("/api/atendimentos/42", request.url.encodedPath)
        } finally {
            client.close()
        }
    }

    @Test
    fun deleteHidesLocallyAndRemovesAfterServerConfirms() = runTest {
        var capturedRequest: HttpRequestData? = null
        val client = HttpClient(
            MockEngine { request ->
                capturedRequest = request
                respond(content = "", status = HttpStatusCode.NoContent)
            }
        )
        val localDataSource = AttendanceLocalDataSource()
        localDataSource.save(
            AttendanceEntity(
                id = "attendance-42",
                nome = "Maria Silva",
                telefone = "11999999999",
                maiorProblema = "Acompanhamento",
                dataAtendimento = "2026-11-07",
                syncStatus = SyncStatus.SYNCED
            )
        )
        val repository = AttendanceRepositoryImpl(
            apiService = AttendanceApiService(client, baseUrl = "http://localhost"),
            localDataSource = localDataSource,
            syncScope = this
        )

        try {
            assertEquals(AttendanceResult.Success, repository.delete("attendance-42"))
            assertEquals(emptyList(), localDataSource.getVisibleAll().map { it.id })
            assertEquals(PendingOperation.DELETE, localDataSource.getPendingSync().single().pendingOperation)

            runCurrent()
            if (capturedRequest == null) repository.syncPending()
            advanceUntilIdle()

            val request = assertNotNull(capturedRequest)
            assertEquals(HttpMethod.Delete, request.method)
            assertEquals("/api/atendimentos/attendance-42", request.url.encodedPath)
            assertEquals(emptyList(), localDataSource.getAll().map { it.id })
        } finally {
            client.close()
        }
    }

    private fun validParams() = AttendanceParams(
        nome = "Maria Silva",
        telefone = "11999999999",
        situacaoNaIgreja = listOf(ChurchSituation.PRIMEIRA_VEZ),
        maiorProblema = "Está passando por dificuldades",
        dataAtendimento = "2026-11-07"
    )
}
