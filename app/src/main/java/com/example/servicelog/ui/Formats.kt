package com.example.servicelog.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.example.servicelog.R
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

@Composable
fun DueStatus.nextText(): String {
    val parts = buildList {
        monthsLeft?.let {
            add(if (it < 0) pluralStringResource(R.plurals.overdue_months, (-it).toInt(), (-it).toInt())
            else pluralStringResource(R.plurals.due_in_months, it.toInt(), it.toInt()))
        }
        kmLeft?.let {
            add(if (it < 0) stringResource(R.string.km_overdue, numberFormat.format(-it))
            else stringResource(R.string.in_km, numberFormat.format(it)))
        }
    }
    return if (parts.isEmpty()) stringResource(R.string.no_data_yet) else parts.joinToString(" · ")
}

@Composable
fun DueStatus.lastText(): String? {
    val parts = buildList {
        lastDate?.let { add(it.format(monthFormatter)) }
        lastMileage?.let { add(stringResource(R.string.mileage_km_format, numberFormat.format(it))) }
    }
    return if (parts.isEmpty()) null else parts.joinToString(" · ")
}
