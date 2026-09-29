package com.example.servicelog.domain

import java.time.LocalDate

data class MaintenanceInterval(
    val id: Long = 0,
    val vehicleId: Long,
    val title: String,
    val category: Category,
    val intervalMonths: Int?,
    val intervalKm: Int?,
    val referenceDate: LocalDate?,
    val referenceMileage: Int?
)