package com.example.servicelog.domain

import java.time.LocalDate

data class MaintenanceEntry(
    val id: Long = 0,
    val intervalId: Long? = null,
    val vehicleId: Long,
    val title: String,
    val category: Category,
    val date: LocalDate?,
    val dateIsApproximate: Boolean = false,
    val mileage: Int?,
    val costCents: Int?,
    val workshop: String?,
    val notes: String?,
    val photoUri: String? = null
) {

}