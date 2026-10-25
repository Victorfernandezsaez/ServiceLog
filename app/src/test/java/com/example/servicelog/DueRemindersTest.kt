package com.example.servicelog

import com.example.servicelog.domain.Category
import com.example.servicelog.domain.DueStatus
import com.example.servicelog.domain.MaintenanceInterval
import com.example.servicelog.domain.ReminderKind
import com.example.servicelog.domain.Urgency
import com.example.servicelog.domain.remindersFor
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test

class DueRemindersTest {

    private fun status(daysLeft: Long?) = DueStatus(
        interval = MaintenanceInterval(
            vehicleId = 1, title = "Oil", category = Category.ENGINE,
            intervalMonths = 12, intervalKm = null,
            referenceDate = null, referenceMileage = null
        ),
        urgency = Urgency.OK,
        dueDate = null,
        monthsLeft = null,
        daysLeft = daysLeft,
        kmLeft = null,
        lastDate = null,
        lastMileage = null
    )

    @Test
    fun `reminds a week before`() {
        val result = remindersFor(listOf(status(7)))
        assertEquals(1, result.size)
        assertEquals(ReminderKind.WEEK_BEFORE, result[0].kind)
    }

    @Test
    fun `reminds a day before`() {
        val result = remindersFor(listOf(status(1)))
        assertEquals(ReminderKind.DAY_BEFORE, result[0].kind)
    }

    @Test
    fun `does not remind on other days`() {
        val result = remindersFor(listOf(status(6), status(8), status(2), status(0)))
        assertTrue(result.isEmpty())
    }

    @Test
    fun `does not remind when already overdue`() {
        val result = remindersFor(listOf(status(-3)))
        assertTrue(result.isEmpty())
    }

    @Test
    fun `mileage-only intervals produce no reminder`() {
        val result = remindersFor(listOf(status(null)))
        assertTrue(result.isEmpty())
    }
}