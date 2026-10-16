package com.example.servicelog.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.servicelog.R
import com.example.servicelog.domain.MaintenanceEntry
import com.example.servicelog.ui.dayFormatter
import com.example.servicelog.ui.moneyFormat
import com.example.servicelog.ui.monthFormatter
import com.example.servicelog.ui.numberFormat
import com.example.servicelog.ui.viewModels.HistoryViewModel

@Composable
fun HistoryScreen(
    onEntryClick: (MaintenanceEntry) -> Unit,
    vm: HistoryViewModel = hiltViewModel()
) {
    val state by vm.uiState.collectAsStateWithLifecycle()

    if (state.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val entries = state.entries

    if (entries.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                stringResource(R.string.no_entries_yet),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        return
    }

    val grouped = entries.groupBy { entry ->
        entry.date?.format(monthFormatter) ?: stringResource(R.string.no_date)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        grouped.forEach { (month, monthEntries) ->

            item {
                val total = monthEntries.sumOf { it.costCents ?: 0 }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = month.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (total > 0) {
                        Text(
                            text = moneyFormat.format(total / 100.0),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            items(monthEntries) { entry ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clickable { onEntryClick(entry) },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    text = entry.category.label.uppercase(),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = entry.title,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = buildString {
                                    entry.date?.let {
                                        if (entry.dateIsApproximate) append("~")
                                        append(it.format(dayFormatter))
                                    }
                                    entry.mileage?.let {
                                        if (isNotEmpty()) append(" · ")
                                        append(
                                            stringResource(
                                                R.string.mileage_km_format,
                                                numberFormat.format(it)
                                            )
                                        )
                                    }
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        entry.costCents?.let {
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = moneyFormat.format(it / 100.0),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(80.dp)) }
    }
}
