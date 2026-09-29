package com.example.servicelog.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface MaintenanceIntervalDao {
    @Query("SELECT * FROM maintenance_interval WHERE vehicleId = :vehicleId")
    fun observeIntervals(vehicleId: Long): Flow<List<MaintenanceIntervalEntity>>

    @Upsert
    suspend fun save(interval: MaintenanceIntervalEntity)
    @Delete
    suspend fun delete(interval: MaintenanceIntervalEntity)

    @Insert
    suspend fun insertReturningId(interval: MaintenanceIntervalEntity): Long
}