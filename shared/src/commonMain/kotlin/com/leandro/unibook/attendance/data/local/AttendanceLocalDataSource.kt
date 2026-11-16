package com.leandro.unibook.attendance.data.local

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class AttendanceLocalDataSource {
    private val mutex = Mutex()
    private val _items = MutableStateFlow<List<AttendanceEntity>>(emptyList())
    val items: StateFlow<List<AttendanceEntity>> = _items.asStateFlow()

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
        }
    }

    suspend fun saveAll(newEntities: List<AttendanceEntity>) {
        mutex.withLock {
            val current = _items.value.toMutableList()
            newEntities.forEach { remote ->
                // Remove o item local temporário (local_) somente quando ele já foi marcado como SYNCED
                val syncedLocalIndex = current.indexOfFirst { local ->
                    local.id.startsWith("local_") &&
                        local.syncStatus == SyncStatus.SYNCED &&
                        local.nome.trim().equals(remote.nome.trim(), ignoreCase = true) &&
                        local.dataAtendimento == remote.dataAtendimento
                }
                if (syncedLocalIndex >= 0) {
                    current.removeAt(syncedLocalIndex)
                }

                val existingIndex = current.indexOfFirst { it.id == remote.id }
                if (existingIndex >= 0) {
                    if (current[existingIndex].syncStatus != SyncStatus.PENDING_SYNC) {
                        current[existingIndex] = remote
                    }
                } else {
                    current.add(remote)
                }
            }
            _items.value = current.sortedByDescending { it.dataAtendimento }
        }
    }

    suspend fun markAsSynced(id: String) {
        mutex.withLock {
            val current = _items.value.map { item ->
                if (item.id == id) item.copy(syncStatus = SyncStatus.SYNCED) else item
            }
            _items.value = current
        }
    }

    suspend fun update(entity: AttendanceEntity) {
        mutex.withLock {
            _items.value = _items.value.map { current ->
                if (current.id == entity.id) entity else current
            }
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

    suspend fun getAll(): List<AttendanceEntity> {
        mutex.withLock {
            return _items.value
        }
    }
}
