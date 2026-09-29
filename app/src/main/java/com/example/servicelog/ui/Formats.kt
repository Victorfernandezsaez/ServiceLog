package com.example.servicelog.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.servicelog.domain.DueStatus
import com.example.servicelog.domain.Urgency
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Currency
import java.util.Locale

val appLocale: Locale = Locale.GERMANY

val moneyFormat: NumberFormat = NumberFormat.getCurrencyInstance(appLocale).apply {
    currency = Currency.getInstance("EUR")
}

val numberFormat: NumberFormat = NumberFormat.getIntegerInstance(appLocale)

val dayFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH)
val monthFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)

@Composable
fun Urgency.color(): Color = when (this) {
    Urgency.OVERDUE -> MaterialTheme.colorScheme.error
    Urgency.DUE_SOON -> Color(0xFFE8A33D)
    Urgency.OK -> Color(0xFF3C8C4E)
    Urgency.UNKNOWN -> MaterialTheme.colorScheme.outline
}

fun DueStatus.nextText(): String {
    val parts = buildList {
        monthsLeft?.let {
            add(if (it < 0) "${-it} months overdue" else "in $it months")
        }
        kmLeft?.let {
            add(if (it < 0) "${numberFormat.format(-it)} km overdue"
            else "in ${numberFormat.format(it)} km")
        }
    }
    return if (parts.isEmpty()) "No data yet" else parts.joinToString(" · ")
}

fun DueStatus.lastText(): String? {
    val parts = buildList {
        lastDate?.let { add(it.format(monthFormatter)) }
        lastMileage?.let { add("${numberFormat.format(it)} km") }
    }
    return if (parts.isEmpty()) null else parts.joinToString(" · ")
}