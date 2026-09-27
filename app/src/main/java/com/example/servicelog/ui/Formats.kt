package com.example.servicelog.ui

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