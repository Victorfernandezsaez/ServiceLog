package com.example.servicelog.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.servicelog.domain.MaintenanceInterval
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.example.servicelog.R


@Composable
fun IntervalsScreen(
    intervals: List<MaintenanceInterval>,
    onIntervalClick: (MaintenanceInterval) -> Unit,
    onAddClick: () -> Unit
) {
    if (intervals.isEmpty()) {
        Column(
            Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(stringResource(R.string.no_intervals_yet), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(R.string.add_interval_hint),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
            Button(onClick = onAddClick) { Text(stringResource(R.string.add_interval)) }
        }
        return
    }

    LazyColumn(Modifier.fillMaxSize()) {
        items(intervals) { interval ->
            ListItem(
                headlineContent = { Text(interval.title) },
                overlineContent = { Text(interval.category.label) },
                supportingContent = { Text(describeInterval(interval)) },
                modifier = Modifier.clickable { onIntervalClick(interval) }
            )
            HorizontalDivider()
        }
    }
}

@Composable
private fun describeInterval(i: MaintenanceInterval): String {
    val parts = buildList {
        i.intervalMonths?.let { add(pluralStringResource(R.plurals.every_months, it, it)) }
        i.intervalKm?.let { add(stringResource(R.string.every_km, numberFormat.format(it))) }
    }
    return if (parts.isEmpty()) stringResource(R.string.no_interval_set) else parts.joinToString(stringResource(R.string.interval_separator))
}