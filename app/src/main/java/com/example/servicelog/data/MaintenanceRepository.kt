package com.example.servicelog.data

import com.example.servicelog.domain.MaintenanceEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MaintenanceRepository(private val dao: MaintenanceDao) {

    fun observeEntries(vehicleId: Long): Flow<List<MaintenanceEntry>> =
        dao.observeEntries(vehicleId).map { list -> list.map { it.toDomain() } }

    suspend fun save(entry: MaintenanceEntry) = dao.save(entry.toEntity())

    suspend fun delete(entry: MaintenanceEntry) = dao.delete(entry.toEntity())
}

private fun MaintenanceEntryEntity.toDomain() = MaintenanceEntry(
    id, vehicleId, title, category, date, dateIsApproximate,
    mileage, costCents, workshop, notes, photoUri
)

private fun MaintenanceEntry.toEntity() = MaintenanceEntryEntity(
    id, vehicleId, title, category, date, dateIsApproximate,
    mileage, costCents, workshop, notes, photoUri
)