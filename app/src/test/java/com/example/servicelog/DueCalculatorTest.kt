package com.example.servicelog

import com.example.servicelog.domain.Category
import com.example.servicelog.domain.MaintenanceEntry
import com.example.servicelog.domain.MaintenanceInterval
import com.example.servicelog.domain.Urgency
import com.example.servicelog.domain.calculateDue
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Test
import java.time.LocalDate

class DueCalculatorTest {

    private fun interval(
        months: Int? = null,
        km: Int? = null,
        refDate: LocalDate? = null,
        refKm: Int? = null
    ) = MaintenanceInterval(
        vehicleId = 1, title = "Oil", category = Category.ENGINE,
        intervalMonths = months, intervalKm = km,
        referenceDate = refDate, referenceMileage = refKm
    )

    @Test
    fun `overdue by date`() {
        val result = calculateDue(
            interval(months = 12, refDate = LocalDate.of(2025, 1, 1)),
            entries = emptyList(),
            today = LocalDate.of(2026, 6, 1),
            currentMileage = null
        )
        assertEquals(Urgency.OVERDUE, result.urgency)
    }

    @Test
    fun `km criterion wins when closer`() {
        val result = calculateDue(
            interval(months = 12, km = 10000,
                refDate = LocalDate.of(2026, 1, 1), refKm = 370000),
            entries = emptyList(),
            today = LocalDate.of(2026, 3, 1),
            currentMileage = 379500
        )
        assertEquals(Urgency.DUE_SOON, result.urgency)
    }

    @Test
    fun `uses last entry as reference instead of interval reference`() {
        val result = calculateDue(
            interval(months = 12, refDate = LocalDate.of(2020, 1, 1)),
            entries = listOf(
                MaintenanceEntry(
                    vehicleId = 1,
                    title = "Oil change",
                    category = Category.ENGINE,
                    date = LocalDate.of(2026, 1, 1),
                    dateIsApproximate = false,
                    mileage = null,
                    costCents = null,
                    workshop = null,
                    notes = null,
                    photoUri = null
                )
            ),
            today = LocalDate.of(2026, 3, 1),
            currentMileage = null
        )
        assertEquals(LocalDate.of(2027, 1, 1), result.dueDate)
        assertEquals(Urgency.OK, result.urgency)
    }

    private fun entryOf(category: Category, date: LocalDate?, mileage: Int? = null) =
        MaintenanceEntry(
            vehicleId = 1, title = "x", category = category,
            date = date, dateIsApproximate = false, mileage = mileage,
            costCents = null, workshop = null, notes = null, photoUri = null
        )

    @Test
    fun `interval with only months ignores missing mileage`() {
        val result = calculateDue(
            interval(months = 12, refDate = LocalDate.of(2026, 1, 1)),
            entries = emptyList(),
            today = LocalDate.of(2026, 3, 1),
            currentMileage = null
        )
        assertEquals(Urgency.OK, result.urgency)
        assertNull(result.kmLeft)
    }

    @Test
    fun `interval with only km works without any date`() {
        val result = calculateDue(
            interval(km = 10000, refKm = 370000),
            entries = emptyList(),
            today = LocalDate.of(2026, 3, 1),
            currentMileage = 375000
        )
        assertEquals(Urgency.OK, result.urgency)
        assertEquals(5000, result.kmLeft)
        assertNull(result.dueDate)
    }

    @Test
    fun `km interval without current mileage is unknown`() {
        val result = calculateDue(
            interval(km = 10000, refKm = 370000),
            entries = emptyList(),
            today = LocalDate.of(2026, 3, 1),
            currentMileage = null
        )
        assertEquals(Urgency.UNKNOWN, result.urgency)
    }

    @Test
    fun `interval without reference is unknown`() {
        val result = calculateDue(
            interval(months = 12),
            entries = emptyList(),
            today = LocalDate.of(2026, 3, 1),
            currentMileage = 375000
        )
        assertEquals(Urgency.UNKNOWN, result.urgency)
    }

    @Test
    fun `overdue by km even when date is fine`() {
        val result = calculateDue(
            interval(months = 12, km = 10000,
                refDate = LocalDate.of(2026, 1, 1), refKm = 370000),
            entries = emptyList(),
            today = LocalDate.of(2026, 3, 1),
            currentMileage = 382000
        )
        assertEquals(Urgency.OVERDUE, result.urgency)
    }

    @Test
    fun `due today is not overdue`() {
        val result = calculateDue(
            interval(months = 12, refDate = LocalDate.of(2025, 3, 1)),
            entries = emptyList(),
            today = LocalDate.of(2026, 3, 1),
            currentMileage = null
        )
        assertEquals(Urgency.DUE_SOON, result.urgency)
    }

    @Test
    fun `entries of other categories are ignored`() {
        val result = calculateDue(
            interval(months = 12, refDate = LocalDate.of(2020, 1, 1)),
            entries = listOf(
                entryOf(Category.BRAKES, LocalDate.of(2026, 1, 1))
            ),
            today = LocalDate.of(2026, 3, 1),
            currentMileage = null
        )
        assertEquals(Urgency.OVERDUE, result.urgency)
    }

    @Test
    fun `most recent entry wins as reference`() {
        val result = calculateDue(
            interval(months = 12, refDate = LocalDate.of(2020, 1, 1)),
            entries = listOf(
                entryOf(Category.ENGINE, LocalDate.of(2024, 1, 1)),
                entryOf(Category.ENGINE, LocalDate.of(2026, 1, 1)),
                entryOf(Category.ENGINE, LocalDate.of(2025, 1, 1))
            ),
            today = LocalDate.of(2026, 3, 1),
            currentMileage = null
        )
        assertEquals(LocalDate.of(2027, 1, 1), result.dueDate)
    }

    @Test
    fun `entry without date does not become reference`() {
        val result = calculateDue(
            interval(months = 12, refDate = LocalDate.of(2026, 1, 1)),
            entries = listOf(entryOf(Category.ENGINE, null)),
            today = LocalDate.of(2026, 3, 1),
            currentMileage = null
        )
        assertEquals(LocalDate.of(2027, 1, 1), result.dueDate)
    }

    private fun entryOf(
        category: Category,
        date: LocalDate?,
        mileage: Int? = null,
        intervalId: Long? = null
    ) = MaintenanceEntry(
        vehicleId = 1,
        title = "x",
        category = category,
        date = date,
        dateIsApproximate = false,
        mileage = mileage,
        costCents = null,
        workshop = null,
        notes = null,
        photoUri = null,
        intervalId = intervalId
    )

    @Test
    fun `linked entry wins over category match`() {
        val oilInterval = interval(km = 15000, refKm = 300000).copy(id = 1)
        val result = calculateDue(
            oilInterval,
            entries = listOf(
                entryOf(Category.ENGINE, LocalDate.of(2026, 3, 10), 375529, intervalId = 2),
                entryOf(Category.ENGINE, LocalDate.of(2025, 8, 1), 371806, intervalId = 1)
            ),
            today = LocalDate.of(2026, 4, 1),
            currentMileage = 376000
        )
        assertEquals(371806, result.lastMileage)
    }

    @Test
    fun `unlinked entries still match by category`() {
        val result = calculateDue(
            interval(months = 12, refDate = LocalDate.of(2020, 1, 1)),
            entries = listOf(entryOf(Category.ENGINE, LocalDate.of(2026, 1, 1))),
            today = LocalDate.of(2026, 3, 1),
            currentMileage = null
        )
        assertEquals(LocalDate.of(2027, 1, 1), result.dueDate)
    }

    @Test
    fun `entry with only mileage serves as km reference`() {
        val result = calculateDue(
            interval(km = 120000, refKm = 200000),
            entries = listOf(entryOf(Category.ENGINE, null, mileage = 357000)),
            today = LocalDate.of(2026, 4, 1),
            currentMileage = 375529
        )
        assertEquals(357000, result.lastMileage)
        assertEquals(120000 - (375529 - 357000), result.kmLeft)
    }

    @Test
    fun `date and mileage references can come from different entries`() {
        val result = calculateDue(
            interval(months = 12, km = 15000),
            entries = listOf(
                entryOf(Category.ENGINE, LocalDate.of(2026, 1, 1)),
                entryOf(Category.ENGINE, null, mileage = 370000)
            ),
            today = LocalDate.of(2026, 4, 1),
            currentMileage = 375000
        )
        assertEquals(LocalDate.of(2026, 1, 1), result.lastDate)
        assertEquals(370000, result.lastMileage)
    }
}