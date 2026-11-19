package com.leandro.unibook.attendance.data.local

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json

class AttendanceLocalDataSource(
    private val storageRepository: AttendanceStorageRepository? = null
) {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    private val mutex = Mutex()
    private val _items = MutableStateFlow<List<AttendanceEntity>>(loadInitialItems())
    val items: StateFlow<List<AttendanceEntity>> = _items.asStateFlow()

    private fun loadInitialItems(): List<AttendanceEntity> {
        val rawJson = storageRepository?.getSavedAttendancesJson() ?: return emptyList()
        return try {
            json.decodeFromString<List<AttendanceEntity>>(rawJson)
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun persistItemsLocked() {
        storageRepository?.let { repo ->
            try {
                val encoded = json.encodeToString(_items.value)
                repo.saveAttendancesJson(encoded)
            } catch (_: Exception) {
            }
        }
    }

    suspend fun save(entity: AttendanceEntity) {
        mutex.withLock {
            val current = _items.value.toMutableList()
            val existingIndex = current.indexOfFirst { it.id == entity.id }
            if (existingIndex >= 0) {
                current[existingIndex] = entity
            } else {
                current.add(0, entity)
            }
            _items.value = current
            persistItemsLocked()
        }
    }

    suspend fun saveAll(newEntities: List<AttendanceEntity>) {
        mutex.withLock {
            val current = _items.value.toMutableList()
            newEntities.forEach { remoteInput ->
                var remote = remoteInput
                // Remove o item local temporário (local_) somente quando ele já foi marcado como SYNCED
                val syncedLocalIndex = current.indexOfFirst { local ->
                    local.id.startsWith("local_") &&
                        local.syncStatus == SyncStatus.SYNCED &&
                        local.nome.trim().equals(remote.nome.trim(), ignoreCase = true) &&
                        local.dataAtendimento == remote.dataAtendimento
                }
                if (syncedLocalIndex >= 0) {
                    val localItem = current.removeAt(syncedLocalIndex)
                    if (remote.fotoBase64.isBlank() && localItem.fotoBase64.isNotBlank()) {
                        remote = remote.copy(fotoBase64 = localItem.fotoBase64)
                    }
                }

                val existingIndex = current.indexOfFirst { it.id == remote.id }
                if (existingIndex >= 0) {
                    val existing = current[existingIndex]
                    if (existing.syncStatus != SyncStatus.PENDING_SYNC) {
                        val mergedPhoto = if (remote.fotoBase64.isBlank() && existing.fotoBase64.isNotBlank()) {
                            existing.fotoBase64
                        } else {
                            remote.fotoBase64
                        }
                        current[existingIndex] = remote.copy(fotoBase64 = mergedPhoto)
                    }
                } else {
                    current.add(remote)
                }
            }
            _items.value = current.sortedByDescending { it.dataAtendimento }
            persistItemsLocked()
        }
    }

    suspend fun replaceLocalIdWithRemoteId(oldLocalId: String, newRemoteId: String) {
        mutex.withLock {
            val current = _items.value.toMutableList()
            val index = current.indexOfFirst { it.id == oldLocalId }
            if (index >= 0) {
                current[index] = current[index].copy(id = newRemoteId, syncStatus = SyncStatus.SYNCED)
                _items.value = current
                persistItemsLocked()
            }
        }
    }

    suspend fun markAsSynced(id: String, expectedEntity: AttendanceEntity? = null): Boolean {
        mutex.withLock {
            val current = _items.value.toMutableList()
            val index = current.indexOfFirst { it.id == id }
            if (index < 0) return false

            val item = current[index]
            val isDelete = expectedEntity?.pendingOperation == PendingOperation.DELETE || item.pendingOperation == PendingOperation.DELETE
            if (isDelete) {
                current.removeAt(index)
            } else {
                current[index] = item.copy(syncStatus = SyncStatus.SYNCED)
            }
            _items.value = current
            persistItemsLocked()
            return true
        }
    }

    suspend fun markAsPendingDelete(id: String): Boolean {
        mutex.withLock {
            val current = _items.value.toMutableList()
            val index = current.indexOfFirst { it.id == id }
            if (index < 0) return false

            current[index] = current[index].copy(
                syncStatus = SyncStatus.PENDING_SYNC,
                pendingOperation = PendingOperation.DELETE
            )
            _items.value = current
            persistItemsLocked()
            return true
        }
    }

    suspend fun update(entity: AttendanceEntity) {
        mutex.withLock {
            _items.value = _items.value.map { current ->
                if (current.id == entity.id) entity else current
            }
            persistItemsLocked()
        }
    }

    suspend fun getById(id: String): AttendanceEntity? {
        mutex.withLock {
            return _items.value.firstOrNull { it.id == id }
        }
    }

    suspend fun getPendingSync(): List<AttendanceEntity> {
        mutex.withLock {
            return _items.value.filter { it.syncStatus == SyncStatus.PENDING_SYNC }
        }
    }

    suspend fun getVisibleAll(): List<AttendanceEntity> {
        mutex.withLock {
            return _items.value.filter { it.pendingOperation != PendingOperation.DELETE }
        }
    }

    suspend fun getAll(): List<AttendanceEntity> {
        mutex.withLock {
            return _items.value
        }
    }
}
