package com.example.servicelog.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface MaintenanceDao {

    @Query("SELECT * FROM maintenance_entry WHERE vehicleId = :vehicleId ORDER BY date DESC, mileage DESC")
    fun observeEntries(vehicleId: Long): Flow<List<MaintenanceEntryEntity>>

    @Query("""
        SELECT strftime('%Y-%m', date(date * 86400, 'unixepoch')) AS month,
               SUM(COALESCE(costCents, 0)) AS totalCents
        FROM maintenance_entry
        WHERE vehicleId = :vehicleId AND date IS NOT NULL
        GROUP BY month
        ORDER BY month DESC
    """)
    fun observeMonthlyCosts(vehicleId: Long): Flow<List<MonthlyTotal>>

    @Upsert
    suspend fun save(entry: MaintenanceEntryEntity)

    @Delete
    suspend fun delete(entry: MaintenanceEntryEntity)
}