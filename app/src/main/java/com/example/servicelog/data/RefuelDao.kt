package com.example.servicelog.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface RefuelDao {
    @Query("SELECT * FROM refuel WHERE vehicleId = :vehicleId ORDER BY mileage ASC")
    fun observeRefuels(vehicleId: Long): Flow<List<RefuelEntity>>

    @Query("""
        SELECT strftime('%Y-%m', date(date * 86400, 'unixepoch')) AS month,
               SUM(costCents) AS totalCents
        FROM refuel
        WHERE vehicleId = :vehicleId
        GROUP BY month
        ORDER BY month DESC
    """)
    fun observeMonthlyCosts(vehicleId: Long): Flow<List<MonthlyTotal>>

    @Upsert
    suspend fun save(refuel: RefuelEntity)

    @Delete
    suspend fun delete(refuel: RefuelEntity)
}