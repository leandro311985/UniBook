package com.leandro.unibook.attendance.data.repository

import com.leandro.unibook.attendance.data.local.AttendanceEntity
import com.leandro.unibook.attendance.data.local.AttendanceLocalDataSource
import com.leandro.unibook.attendance.data.local.SyncStatus
import com.leandro.unibook.attendance.data.remote.AttendanceApiService
import com.leandro.unibook.attendance.data.remote.dto.AttendanceRequestDto
import com.leandro.unibook.attendance.domain.model.AttendanceItem
import com.leandro.unibook.attendance.domain.model.AttendanceParams
import com.leandro.unibook.attendance.domain.model.AttendanceResult
import com.leandro.unibook.attendance.domain.repository.AttendanceRepository
import com.leandro.unibook.attendance.presentation.ui.decodePhotoBytes
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.random.Random
import kotlin.time.Duration.Companion.minutes
import kotlin.time.TimeMark
import kotlin.time.TimeSource

class AttendanceRepositoryImpl(
    private val apiService: AttendanceApiService,
    private val localDataSource: AttendanceLocalDataSource
) : AttendanceRepository {

    private var lastFetchTimeMark: TimeMark? = null
    private val cacheTtl = 5.minutes

    override suspend fun create(params: AttendanceParams): AttendanceResult {
        return try {
            val localId = "local_" + Random.nextLong(100000, 999999)
            val entity = AttendanceEntity(
                id = localId,
                nome = params.nome.trim(),
                endereco = params.endereco.trim(),
                bairro = params.bairro.trim(),
                telefone = params.telefone.trim(),
                email = params.email.trim(),
                situacaoNaIgreja = params.situacaoNaIgreja.map { it.name },
                maiorProblema = params.maiorProblema.trim(),
                cultosAFrequentar = params.cultosAFrequentar.trim(),
                responsavelAcompanhamento = params.responsavelAcompanhamento.trim(),
                dataAtendimento = params.dataAtendimento,
                vistoDoPastor = params.vistoDoPastor.ifEmpty { "Pendente" },
                fotoBase64 = params.fotoBase64,
                syncStatus = SyncStatus.PENDING_SYNC
            )

            // 1. Salva localmente primeiro (Offline-first)
            localDataSource.save(entity)

            // 2. Tenta sincronizar com o servidor em background
            syncPending()

            AttendanceResult.Success
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // Mesmo em falha de rede, já salvou localmente!
            AttendanceResult.Success
        }
    }

    override suspend fun update(id: String, params: AttendanceParams): AttendanceResult {
        return try {
            val existing = localDataSource.getById(id)
            val updated = AttendanceEntity(
                id = id,
                nome = params.nome.trim(),
                endereco = params.endereco.trim(),
                bairro = params.bairro.trim(),
                telefone = params.telefone.trim(),
                email = params.email.trim(),
                situacaoNaIgreja = params.situacaoNaIgreja.map { it.name },
                maiorProblema = params.maiorProblema.trim(),
                cultosAFrequentar = params.cultosAFrequentar.trim(),
                responsavelAcompanhamento = params.responsavelAcompanhamento.trim(),
                dataAtendimento = params.dataAtendimento,
                vistoDoPastor = params.vistoDoPastor.ifEmpty { "Pendente" },
                fotoBase64 = params.fotoBase64.ifEmpty { existing?.fotoBase64 ?: "" },
                syncStatus = existing?.syncStatus ?: SyncStatus.SYNCED
            )

            if (existing?.syncStatus == SyncStatus.PENDING_SYNC) {
                localDataSource.update(updated)
                return AttendanceResult.Success
            }

            apiService.update(
                id = id,
                request = params.toRequestDto()
            )
            localDataSource.update(updated.copy(syncStatus = SyncStatus.SYNCED))
            AttendanceResult.Success
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            AttendanceResult.Error(e.message ?: "Não foi possível atualizar o atendimento.")
        }
    }

    override fun observeAll(): Flow<List<AttendanceItem>> {
        return localDataSource.items.map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getAll(forceFetch: Boolean): List<AttendanceItem> {
        val hasPending = localDataSource.getPendingSync().isNotEmpty()
        val elapsed = lastFetchTimeMark?.elapsedNow()
        val isCacheValid = !forceFetch && !hasPending && elapsed != null && elapsed < cacheTtl && localDataSource.getAll().isNotEmpty()

        if (isCacheValid) {
            return localDataSource.getAll().map { it.toDomain() }
        }

        // Tenta sincronizar pendentes e buscar atualizações da API
        try {
            syncPending()
            val remoteList = apiService.getAll()
            val remoteEntities = remoteList.map { dto ->
                AttendanceEntity(
                    id = dto.id ?: ("remote_" + dto.nome.hashCode()),
                    nome = dto.nome,
                    endereco = dto.endereco ?: "",
                    bairro = dto.bairro ?: "",
                    telefone = dto.telefone,
                    email = dto.email ?: "",
                    situacaoNaIgreja = dto.situacaoNaIgreja,
                    maiorProblema = dto.maiorProblema,
                    cultosAFrequentar = dto.cultosAFrequentar ?: "",
                    responsavelAcompanhamento = dto.responsavelAcompanhamento ?: "",
                    dataAtendimento = dto.dataAtendimento,
                    vistoDoPastor = dto.vistoDoPastor ?: "Pendente",
                    fotoBase64 = dto.effectiveFotoBase64,
                    syncStatus = SyncStatus.SYNCED
                )
            }
            localDataSource.saveAll(remoteEntities)
            lastFetchTimeMark = TimeSource.Monotonic.markNow()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // Em caso de erro de rede, utiliza os dados locais
        }

        return localDataSource.getAll().map { it.toDomain() }
    }

    override suspend fun syncPending(): Int {
        val pending = localDataSource.getPendingSync()
        var syncedCount = 0

        for (item in pending) {
            try {
                val dto = item.toRequestDto()
                val photoBytes = decodePhotoBytes(item.fotoBase64)

                try {
                    apiService.createMultipart(
                        request = dto,
                        fotoBytes = photoBytes
                    )
                } catch (e: Exception) {
                    if (e is CancellationException) throw e
                    apiService.create(request = dto)
                }

                localDataSource.markAsSynced(item.id)
                syncedCount++
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // Mantém como PENDING_SYNC para tentar novamente depois
            }
        }
        return syncedCount
    }

    override suspend fun confirmReminder(id: String) {
        try {
            apiService.confirmReminder(id)
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            // Ignora falhas pontuais de rede se o lembrete já foi confirmado
        }
    }

    private fun AttendanceEntity.toDomain() = AttendanceItem(
        id = id,
        nome = nome,
        endereco = endereco,
        bairro = bairro,
        telefone = telefone,
        email = email,
        situacaoNaIgreja = situacaoNaIgreja,
        maiorProblema = maiorProblema,
        cultosAFrequentar = cultosAFrequentar,
        responsavelAcompanhamento = responsavelAcompanhamento,
        dataAtendimento = dataAtendimento,
        vistoDoPastor = vistoDoPastor,
        fotoBase64 = fotoBase64
    )

    private fun AttendanceParams.toRequestDto() = AttendanceRequestDto(
        nome = nome.trim(),
        endereco = endereco.trim().ifEmpty { null },
        bairro = bairro.trim().ifEmpty { null },
        telefone = telefone.trim(),
        email = email.trim().ifEmpty { null },
        situacaoNaIgreja = situacaoNaIgreja.map { it.name },
        maiorProblema = maiorProblema.trim(),
        cultosAFrequentar = cultosAFrequentar.trim().ifEmpty { null },
        responsavelAcompanhamento = responsavelAcompanhamento.trim().ifEmpty { null },
        dataAtendimento = dataAtendimento,
        vistoDoPastor = vistoDoPastor.ifEmpty { "Pendente" },
        fotoBase64 = fotoBase64.ifEmpty { null },
        foto = fotoBase64.ifEmpty { null }
    )

    private fun AttendanceEntity.toRequestDto() = AttendanceRequestDto(
        nome = nome,
        endereco = endereco.ifEmpty { null },
        bairro = bairro.ifEmpty { null },
        telefone = telefone,
        email = email.ifEmpty { null },
        situacaoNaIgreja = situacaoNaIgreja,
        maiorProblema = maiorProblema,
        cultosAFrequentar = cultosAFrequentar.ifEmpty { null },
        responsavelAcompanhamento = responsavelAcompanhamento.ifEmpty { null },
        dataAtendimento = dataAtendimento,
        vistoDoPastor = vistoDoPastor,
        fotoBase64 = fotoBase64.ifEmpty { null },
        foto = fotoBase64.ifEmpty { null }
    )
}
