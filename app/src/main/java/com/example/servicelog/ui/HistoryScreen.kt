package com.example.servicelog.ui

import androidx.compose.ui.platform.LocalConfiguration
import java.text.NumberFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.servicelog.domain.MaintenanceEntry
import androidx.compose.ui.res.stringResource
import com.example.servicelog.R
import java.time.format.DateTimeFormatter

@Composable
fun HistoryScreen(
    entries: List<MaintenanceEntry>,
    onEntryClick: (MaintenanceEntry) -> Unit
) {
    if (entries.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.no_entries_yet), style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    val grouped = entries.groupBy { entry ->
        entry.date?.format(monthFormatter) ?: stringResource(R.string.no_date)
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        grouped.forEach { (month, monthEntries) ->
            item {
                val total = monthEntries.sumOf { it.costCents ?: 0 }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(month, fontWeight = FontWeight.Bold)
                    if (total > 0) Text(moneyFormat.format(total / 100.0))                }
            }
            items(monthEntries) { entry ->
                ListItem(
                    headlineContent = { Text(entry.title) },
                    supportingContent = {
                        Text(buildString {
                            entry.date?.let {
                                if (entry.dateIsApproximate) append("~")
                                append(it.format(dayFormatter))
                            }
                            entry.mileage?.let {
                                if (isNotEmpty()) append(" · ")
                                append(stringResource(R.string.mileage_km_format, numberFormat.format(it)))
                            }
                        })
                    },
                    overlineContent = { Text(entry.category.label) },
                    trailingContent = {
                        entry.costCents?.let { Text(moneyFormat.format(it / 100.0)) }
                    },
                    modifier = Modifier.clickable { onEntryClick(entry) }
                )
            }
            item { HorizontalDivider() }
        }
    }
}