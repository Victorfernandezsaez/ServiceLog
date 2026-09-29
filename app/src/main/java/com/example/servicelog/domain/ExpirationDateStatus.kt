package com.example.servicelog.domain

import java.time.LocalDate

enum class Urgency { OVERDUE, DUE_SOON, OK, UNKNOWN }

data class DueStatus(
    val interval: MaintenanceInterval,
    val urgency: Urgency,
    val dueDate: LocalDate?,
    val monthsLeft: Long?,
    val kmLeft: Int?,
    val lastDate: LocalDate?,
    val lastMileage: Int?
)