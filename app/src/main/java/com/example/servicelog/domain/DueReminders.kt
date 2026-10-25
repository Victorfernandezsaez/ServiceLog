package com.example.servicelog.domain

const val REMIND_WEEK_BEFORE = 7L
const val REMIND_DAY_BEFORE = 1L

enum class ReminderKind { WEEK_BEFORE, DAY_BEFORE }

data class Reminder(
    val status: DueStatus,
    val kind: ReminderKind
)

/**
 * Picks the intervals that should produce a notification today.
 *
 * Only date-based due points can be announced in advance: the app has no way
 * of knowing the odometer reading between user entries, so mileage-based
 * intervals are deliberately excluded here.
 *
 * The comparison is against exact day counts because this runs once a day.
 */
fun remindersFor(statuses: List<DueStatus>): List<Reminder> =
    statuses.mapNotNull { status ->
        when (status.daysLeft) {
            REMIND_WEEK_BEFORE -> Reminder(status, ReminderKind.WEEK_BEFORE)
            REMIND_DAY_BEFORE -> Reminder(status, ReminderKind.DAY_BEFORE)
            else -> null
        }
    }