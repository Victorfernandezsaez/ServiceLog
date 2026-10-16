package com.example.servicelog.data.daos

import androidx.room.*
import com.example.servicelog.data.entities.VehicleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicle LIMIT 1")
    fun observeVehicle(): Flow<VehicleEntity?>

    @Upsert
    suspend fun save(vehicle: VehicleEntity)

    @Delete
    suspend fun delete(vehicle: VehicleEntity)

    @Insert
    suspend fun insertReturningId(vehicle: VehicleEntity): Long

}