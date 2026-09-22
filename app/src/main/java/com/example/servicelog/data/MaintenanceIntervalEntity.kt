package com.example.servicelog.data

import androidx.room.*
import com.example.servicelog.domain.Category
import java.time.LocalDate

@Entity(
    tableName = "maintenance_interval",
    foreignKeys = [ForeignKey(
        entity = VehicleEntity::class,
        parentColumns = ["id"],
        childColumns = ["vehicleId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("vehicleId")]
)
data class MaintenanceIntervalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehicleId: Long,
    val title: String,
    val category: Category,
    val intervalMonths: Int?,
    val intervalKm: Int?,
    val referenceDate: LocalDate?,
    val referenceMileage: Int?
) {
}