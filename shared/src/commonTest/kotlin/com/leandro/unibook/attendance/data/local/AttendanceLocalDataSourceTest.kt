package com.leandro.unibook.attendance.data.local

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AttendanceLocalDataSourceTest {
    @Test
    fun saveAllReplacesMatchingSyncedLocalAttendanceWithRemoteId() = runTest {
        val dataSource = AttendanceLocalDataSource()
        val local = attendance(id = "local_123", syncStatus = SyncStatus.SYNCED)
        val remote = local.copy(id = "server-123")

        dataSource.save(local)
        dataSource.saveAll(listOf(remote))

        assertEquals(listOf(remote), dataSource.getAll())
    }

    @Test
    fun saveAllKeepsPendingLocalAttendanceWhenRemoteRecordMatches() = runTest {
        val dataSource = AttendanceLocalDataSource()
        val pending = attendance(id = "local_123", syncStatus = SyncStatus.PENDING_SYNC)
        val remote = pending.copy(id = "server-123", syncStatus = SyncStatus.SYNCED)

        dataSource.save(pending)
        dataSource.saveAll(listOf(remote))

        assertEquals(setOf(pending, remote), dataSource.getAll().toSet())
    }

    private fun attendance(id: String, syncStatus: SyncStatus) = AttendanceEntity(
        id = id,
        nome = "Maria Silva",
        telefone = "11999999999",
        maiorProblema = "Pedido de oração",
        dataAtendimento = "2026-11-07",
        syncStatus = syncStatus
    )
}
