package com.example.servicelog.data

import com.example.servicelog.domain.MaintenanceInterval
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class IntervalRepository(private val dao: MaintenanceIntervalDao) {

    fun observeIntervals(vehicleId: Long): Flow<List<MaintenanceInterval>> =
        dao.observeIntervals(vehicleId).map { list -> list.map { it.toIntervalDomain() } }

    suspend fun save(interval: MaintenanceInterval) = dao.save(interval.toIntervalEntity())

    suspend fun delete(interval: MaintenanceInterval) = dao.delete(interval.toIntervalEntity())
}

private fun MaintenanceIntervalEntity.toIntervalDomain() = MaintenanceInterval(
    id = id,
    vehicleId = vehicleId,
    title = title,
    category = category,
    intervalMonths = intervalMonths,
    intervalKm = intervalKm,
    referenceDate = referenceDate,
    referenceMileage = referenceMileage
)

private fun MaintenanceInterval.toIntervalEntity() = MaintenanceIntervalEntity(
    id = id,
    vehicleId = vehicleId,
    title = title,
    category = category,
    intervalMonths = intervalMonths,
    intervalKm = intervalKm,
    referenceDate = referenceDate,
    referenceMileage = referenceMileage
)