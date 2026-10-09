package com.example.servicelog.data

import com.example.servicelog.domain.Refuel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RefuelRepository(private val dao: RefuelDao) {

    fun observeRefuels(vehicleId: Long): Flow<List<Refuel>> =
        dao.observeRefuels(vehicleId).map { list -> list.map { it.toRefuelDomain() } }

    suspend fun save(refuel: Refuel) = dao.save(refuel.toRefuelEntity())

    suspend fun delete(refuel: Refuel) = dao.delete(refuel.toRefuelEntity())

    fun observeMonthlyFuel(vehicleId: Long): Flow<Map<String, Int>> =
        dao.observeMonthlyCosts(vehicleId).map { list ->
            list.associate { it.month to it.totalCents }
        }
}

private fun RefuelEntity.toRefuelDomain() = Refuel(
    id = id,
    vehicleId = vehicleId,
    date = date,
    mileage = mileage,
    liters = liters,
    costCents = costCents,
    fullTank = fullTank
)

private fun Refuel.toRefuelEntity() = RefuelEntity(
    id = id,
    vehicleId = vehicleId,
    date = date,
    mileage = mileage,
    liters = liters,
    costCents = costCents,
    fullTank = fullTank
)