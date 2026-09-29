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
            Text("No intervals yet", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text(
                "Add a rule like \"Oil change every 12 months or 10,000 km\" " +
                        "to see what's coming up on the home screen.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
            Button(onClick = onAddClick) { Text("Add interval") }
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

private fun describeInterval(i: MaintenanceInterval): String {
    val parts = buildList {
        i.intervalMonths?.let { add("every $it months") }
        i.intervalKm?.let { add("every ${numberFormat.format(it)} km") }
    }
    return if (parts.isEmpty()) "No interval set" else parts.joinToString(" or ")
}