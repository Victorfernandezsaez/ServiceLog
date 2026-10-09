package com.example.servicelog.domain

import java.time.LocalDate

data class Refuel(
    val id: Long = 0,
    val vehicleId: Long,
    val date: LocalDate,
    val mileage: Int,
    val liters: Double,
    val costCents: Int,
    val fullTank: Boolean
)