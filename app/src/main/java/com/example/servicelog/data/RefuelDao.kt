package com.example.servicelog.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface RefuelDao {
    @Query("SELECT * FROM maintenance_entry WHERE vehicleId = :vehicleId ORDER BY date DESC, mileage ASC")
    fun observeEntries(vehicleId: Long): Flow<List<MaintenanceEntryEntity>>

    @Upsert
    suspend fun save(entry: MaintenanceEntryEntity)
    @Delete
    suspend fun delete(entry: MaintenanceEntryEntity)
}