package com.example.servicelog.data.repositories

import com.example.servicelog.data.daos.VehicleDao
import com.example.servicelog.data.entities.VehicleEntity
import com.example.servicelog.domain.Vehicle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class VehicleRepository(private val dao: VehicleDao) {

    fun observeVehicle(): Flow<Vehicle?> =
        dao.observeVehicle().map { it?.toDomain() }

    suspend fun save(vehicle: Vehicle) = dao.save(vehicle.toEntity())

    suspend fun saveReturningId(vehicle: Vehicle): Long =
        if (vehicle.id == 0L) {
            dao.insertReturningId(vehicle.toEntity())
        } else {
            dao.save(vehicle.toEntity())
            vehicle.id
        }

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
)