package com.example.servicelog.data

import com.example.servicelog.domain.Vehicle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class VehicleRepository(private val dao: VehicleDao) {

    fun observeVehicle(): Flow<Vehicle?> =
        dao.observeVehicle().map { it?.toDomain() }

    suspend fun save(vehicle: Vehicle) = dao.save(vehicle.toEntity())
}

private fun VehicleEntity.toDomain() = Vehicle(
    id, type, alias, brand, model, year, typeOfFuel, tankCapacity, currentKm
)

private fun Vehicle.toEntity() = VehicleEntity(
    id, type, alias, brand, model, year, typeOfFuel, tankCapacity, currentKm
)