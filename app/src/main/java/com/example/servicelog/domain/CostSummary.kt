package com.example.servicelog.domain

data class CostSummary(
    val refuels: List<Refuel>,
    val monthlyFuelCents: Map<String, Int>,
    val monthlyMaintenanceCents: Map<String, Int>,
    val average: Consumption?,
    val lastStretch: Consumption?
)