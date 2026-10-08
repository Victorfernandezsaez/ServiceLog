package com.example.servicelog.domain

import java.time.LocalDate


data class Vehicle(
    val id: Long = 0,
    val type: VehicleType,
    val alias: String,
    val brand: String,
    val model: String,
    val year: Int?,
    val fuelType: FuelType?,
    val tankCapacity: Int?,
    val currentKm: Int,
    val lastReadingDate: LocalDate? = null
)