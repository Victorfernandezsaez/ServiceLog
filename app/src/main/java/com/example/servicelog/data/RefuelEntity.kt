package com.example.servicelog.data

import androidx.room.*
import java.time.LocalDate

@Entity(
    tableName = "refuel",
    foreignKeys = [ForeignKey(
        entity = VehicleEntity::class,
        parentColumns = ["id"],
        childColumns = ["vehicleId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("vehicleId")]
)
data class RefuelEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehicleId: Long,
    val date: LocalDate,
    val mileage: Int,
    val liters: Double,
    val costCents: Int,
    val fullTank: Boolean
)