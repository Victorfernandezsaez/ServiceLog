package com.example.servicelog.data.daos

import androidx.room.*
import com.example.servicelog.data.entities.MileageReadingEntity
import kotlinx.coroutines.flow.Flow

data class MileageReading(val mileage: Int, val date: Long?)

@Dao
interface MileageDao {

    @Insert
    suspend fun insert(reading: MileageReadingEntity)

    @Query("""
        SELECT mileage, date FROM (
            SELECT mileage, date FROM maintenance_entry
                WHERE vehicleId = :vehicleId AND mileage IS NOT NULL
            UNION ALL
            SELECT mileage, date FROM refuel WHERE vehicleId = :vehicleId
            UNION ALL
            SELECT mileage, date FROM mileage_reading WHERE vehicleId = :vehicleId
        )
        ORDER BY mileage DESC
        LIMIT 1
    """)
    fun observeLatestReading(vehicleId: Long): Flow<MileageReading?>
}