package com.example.servicelog.domain

import java.time.LocalDate
import java.time.temporal.ChronoUnit

const val DUE_SOON_MONTHS = 2L
const val DUE_SOON_KM = 1000

fun calculateDue(
    interval: MaintenanceInterval,
    entries: List<MaintenanceEntry>,
    today: LocalDate,
    currentMileage: Int?
): DueStatus {

    val matching = entries.filter { entry ->
        if (entry.intervalId != null) entry.intervalId == interval.id
        else entry.category == interval.category
    }

    val lastDated = matching.filter { it.date != null }.maxByOrNull { it.date!! }
    val lastWithKm = matching.filter { it.mileage != null }.maxByOrNull { it.mileage!! }

    val refDate = lastDated?.date ?: interval.referenceDate
    val refKm = lastWithKm?.mileage ?: interval.referenceMileage

    val dueDate = if (interval.intervalMonths != null && refDate != null) {
        refDate.plusMonths(interval.intervalMonths.toLong())
    } else null

    val dueMileage = if (interval.intervalKm != null && refKm != null) {
        refKm + interval.intervalKm
    } else null

    val monthsLeft = dueDate?.let { ChronoUnit.MONTHS.between(today, it) }
    val daysLeft = dueDate?.let { ChronoUnit.DAYS.between(today, it) }
    val kmLeft = if (dueMileage != null && currentMileage != null) {
        dueMileage - currentMileage
    } else null

    val byDate = when {
        daysLeft == null -> Urgency.UNKNOWN
        daysLeft < 0 -> Urgency.OVERDUE
        monthsLeft!! < DUE_SOON_MONTHS -> Urgency.DUE_SOON
        else -> Urgency.OK
    }

    val byKm = when {
        kmLeft == null -> Urgency.UNKNOWN
        kmLeft < 0 -> Urgency.OVERDUE
        kmLeft < DUE_SOON_KM -> Urgency.DUE_SOON
        else -> Urgency.OK
    }

    val urgency = listOf(byDate, byKm)
        .filter { it != Urgency.UNKNOWN }
        .minByOrNull { it.ordinal }
        ?: Urgency.UNKNOWN

    val usedDate = if (interval.intervalMonths != null) refDate else null
    val usedKm = if (interval.intervalKm != null) refKm else null


    return DueStatus(
        interval = interval,
        urgency = urgency,
        dueDate = dueDate,
        monthsLeft = monthsLeft,
        kmLeft = kmLeft,
        lastDate = usedDate,
        lastMileage = usedKm
    )
}