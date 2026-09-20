package com.example.servicelog.domain


data class Vehicle(
    val id: Long = 0,
    val type: VehicleType,
    val alias: String,
    val brand: String,
    val model: String,
    val year: Int?,
    val typeOfFuel: String,
    val tankCapacity: Int?,
    val currentKm: Int
)