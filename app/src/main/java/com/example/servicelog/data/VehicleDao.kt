package com.example.servicelog.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicle LIMIT 1")
    fun observeVehicle(): Flow<VehicleEntity?>

    @Upsert
    suspend fun save(vehicle: VehicleEntity)

    @Delete
    suspend fun delete(vehicle: VehicleEntity)
}