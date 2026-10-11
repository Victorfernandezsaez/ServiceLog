package com.example.servicelog.data

import androidx.room.*
import com.example.servicelog.domain.Category
import java.time.LocalDate

@Entity(
    tableName = "maintenance_entry",
    foreignKeys = [ForeignKey(
        entity = VehicleEntity::class,
        parentColumns = ["id"],
        childColumns = ["vehicleId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("vehicleId")]
)

data class MaintenanceEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val intervalId: Long? = null,
    val vehicleId: Long,
    val title: String,
    val category: Category,
    val date: LocalDate?,
    val dateIsApproximate: Boolean = false,
    val mileage: Int?,
    /**
     * Stored in cents as an integer. Floating-point money accumulates rounding
     * errors across sums, which would show up in the monthly totals.
     */
    val costCents: Int?,
    val workshop: String?,
    val notes: String?,
    val photoUri: String? = null
)