package com.example.servicelog.domain

const val MIN_DISTANCE_FOR_AVERAGE = 2000

data class Consumption(
    val litersPer100Km: Double,
    val distanceKm: Int,
    val liters: Double
)

/**
 * Average over all refuels. Approximate while the distance is short:
 * the unknown tank-level difference is bounded by the tank size,
 * so the error shrinks as distance grows.
 * Returns null when there is not enough data to be meaningful.
 */
fun averageConsumption(refuels: List<Refuel>): Consumption? {
    if (refuels.size < 2) return null

    val sorted = refuels.sortedBy { it.mileage }
    val distance = sorted.last().mileage - sorted.first().mileage
    if (distance < MIN_DISTANCE_FOR_AVERAGE) return null

    // the first refuel filled a tank we did not measure, so its liters are excluded
    val liters = sorted.drop(1).sumOf { it.liters }
    if (liters <= 0) return null

    return Consumption(liters / distance * 100, distance, liters)
}

/**
 * Exact consumption for each stretch between two full-tank refuels.
 * Partial refuels inside a stretch still count towards its liters.
 */
fun stretchConsumptions(refuels: List<Refuel>): List<Consumption> {
    val sorted = refuels.sortedBy { it.mileage }
    val result = mutableListOf<Consumption>()

    var startIndex: Int? = null
    var litersInStretch = 0.0

    sorted.forEachIndexed { index, refuel ->
        if (startIndex == null) {
            if (refuel.fullTank) startIndex = index
            return@forEachIndexed
        }

        litersInStretch += refuel.liters

        if (refuel.fullTank) {
            val distance = refuel.mileage - sorted[startIndex!!].mileage
            if (distance > 0 && litersInStretch > 0) {
                result += Consumption(litersInStretch / distance * 100, distance, litersInStretch)
            }
            startIndex = index
            litersInStretch = 0.0
        }
    }

    return result
}

fun lastStretchConsumption(refuels: List<Refuel>): Consumption? =
    stretchConsumptions(refuels).lastOrNull()