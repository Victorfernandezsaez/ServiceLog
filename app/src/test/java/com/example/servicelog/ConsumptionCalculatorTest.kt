package com.example.servicelog

import com.example.servicelog.domain.Refuel
import com.example.servicelog.domain.averageConsumption
import com.example.servicelog.domain.stretchConsumptions
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import org.junit.Test
import java.time.LocalDate
class ConsumptionCalculatorTest {

    private fun refuel(mileage: Int, liters: Double, full: Boolean = true) =
        Refuel(
            vehicleId = 1, date = LocalDate.of(2026, 1, 1),
            mileage = mileage, liters = liters, costCents = 0, fullTank = full
        )

    @Test
    fun `single refuel gives no average`() {
        assertNull(averageConsumption(listOf(refuel(370000, 60.0))))
    }

    @Test
    fun `short distance gives no average`() {
        val result = averageConsumption(listOf(
            refuel(370000, 60.0),
            refuel(370500, 40.0)
        ))
        assertNull(result)
    }

    @Test
    fun `average excludes the first tank`() {
        val result = averageConsumption(listOf(
            refuel(370000, 60.0),
            refuel(375000, 50.0)
        ))!!
        assertEquals(5000, result.distanceKm)
        assertEquals(50.0, result.liters, 0.01)
        assertEquals(1.0, result.litersPer100Km, 0.01)
    }

    @Test
    fun `stretch between two full tanks`() {
        val result = stretchConsumptions(listOf(
            refuel(370000, 60.0),
            refuel(370800, 56.0)
        ))
        assertEquals(1, result.size)
        assertEquals(7.0, result[0].litersPer100Km, 0.01)
    }

    @Test
    fun `partial refuel counts towards the stretch`() {
        val result = stretchConsumptions(listOf(
            refuel(370000, 60.0),
            refuel(370400, 20.0, full = false),
            refuel(370800, 36.0)
        ))
        assertEquals(1, result.size)
        assertEquals(7.0, result[0].litersPer100Km, 0.01)
    }

    @Test
    fun `no full tanks gives no stretches`() {
        val result = stretchConsumptions(listOf(
            refuel(370000, 30.0, full = false),
            refuel(370400, 30.0, full = false)
        ))
        assertTrue(result.isEmpty())
    }

    @Test
    fun `leading partials are ignored until the first full tank`() {
        val result = stretchConsumptions(listOf(
            refuel(369500, 25.0, full = false),
            refuel(370000, 60.0),
            refuel(370800, 56.0)
        ))
        assertEquals(1, result.size)
        assertEquals(7.0, result[0].litersPer100Km, 0.01)
    }
}