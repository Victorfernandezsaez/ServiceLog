package com.example.servicelog.data.entities

import androidx.room.*
import java.time.LocalDate

@Entity(
    tableName = "mileage_reading",
    foreignKeys = [ForeignKey(
        entity = VehicleEntity::class,
        parentColumns = ["id"],
        childColumns = ["vehicleId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("vehicleId")]
)
data class MileageReadingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehicleId: Long,
    val date: LocalDate,
    val mileage: Int
)