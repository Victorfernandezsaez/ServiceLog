package com.example.servicelog.domain

/**
 * Minimum distance before an average is shown. With a tank of roughly
 * 80 liters, the worst-case error is about ±2.5 L/100km at 2000 km and
 * keeps shrinking from there.
 */
const val MIN_DISTANCE_FOR_AVERAGE = 2000

data class Consumption(
    val litersPer100Km: Double,
    val distanceKm: Int,
    val liters: Double
)

/**
 * Average consumption over all recorded refuels.
 *
 * The liters of the first refuel are excluded: they filled a tank that was
 * consumed before the first measured kilometre, so counting them would
 * inflate the result.
 *
 * The figure is approximate. The unknown difference in tank level between
 * the first and last refuel is bounded by the tank capacity, so the error
 * does not grow while the distance does: it converges as more refuels are
 * added. Below MIN_DISTANCE_FOR_AVERAGE the error is too large to be
 * meaningful, so no value is returned at all rather than a misleading one.
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
 *
 * A full tank is the only point where the fuel level is known, which makes
 * it the only valid boundary for an exact calculation. Partial refuels
 * inside a stretch still count towards its liters; refuels before the
 * first full tank are discarded, as there is no measurable starting point.
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
            val distance = refuel.mileage - sorted[startIndex].mileage
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