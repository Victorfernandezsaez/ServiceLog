package com.example.servicelog.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.servicelog.domain.VehicleType
import java.time.LocalDate

@Entity(tableName = "vehicle")
data class VehicleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: VehicleType,
    val alias: String,
    val brand: String,
    val model: String,
    val year: Int?,
    val typeOfFuel: String,
    val tankCapacity: Int?,
    val currentKm: Int,
    val lastReadingDate: LocalDate? = null

)