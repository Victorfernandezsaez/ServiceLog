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
    id = id,
    type = type,
    alias = alias,
    brand = brand,
    model = model,
    year = year,
    fuelType = fuelType,
    tankCapacity = tankCapacity,
    currentKm = currentKm,
    lastReadingDate = lastReadingDate
)

private fun Vehicle.toEntity() = VehicleEntity(
    id = id,
    type = type,
    alias = alias,
    brand = brand,
    model = model,
    year = year,
    fuelType = fuelType,
    tankCapacity = tankCapacity,
    currentKm = currentKm,
    lastReadingDate = lastReadingDate
)