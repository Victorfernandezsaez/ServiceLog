package com.example.servicelog.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.servicelog.R
import com.example.servicelog.domain.DueStatus
import com.example.servicelog.domain.Vehicle
import com.example.servicelog.domain.icon

@Composable
fun VehicleInfo(
    vehicle: Vehicle,
    dueStatuses: List<DueStatus>,
    onUpdateMileage: () -> Unit
) {

    var expanded by remember { mutableStateOf(false) }
    val visible = if (expanded) dueStatuses else dueStatuses.take(4)

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = vehicle.type.icon,
                        contentDescription = vehicle.type.label,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = vehicle.alias,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = listOf(vehicle.brand, vehicle.model)
                                .filter { it.isNotBlank() }
                                .joinToString(" "),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            stringResource(
                                R.string.mileage_km_format,
                                numberFormat.format(vehicle.currentKm)
                            ),
                            style = MaterialTheme.typography.displaySmall
                        )
                        vehicle.lastReadingDate?.let {
                            Text(
                                stringResource(R.string.mileage_as_of, it.format(dayFormatter)),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    OutlinedButton(
                        onClick = onUpdateMileage,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Text(stringResource(R.string.update))


                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            stringResource(R.string.next_service),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Spacer(Modifier.height(8.dp))

        if (dueStatuses.isEmpty()) {
            Text(
                stringResource(R.string.no_intervals_yet),
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                visible.forEach { status ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
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
                            Box(
                                modifier = Modifier
                                    .padding(top = 5.dp)
                                    .size(8.dp)
                                    .background(status.urgency.color(), CircleShape)
                            )
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(status.interval.title, fontSize = 14.sp)
                                Text(
                                    status.nextText(),
                                    fontSize = 12.sp,
                                    color = status.urgency.color()
                                )
                                status.lastText()?.let {
                                    Text(
                                        stringResource(R.string.last_prefix, it),
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
            if (dueStatuses.size > 4) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (expanded) stringResource(R.string.show_less) else stringResource(
                            R.string.show_more,
                            dueStatuses.size - 4
                        ),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .clickable { expanded = !expanded }
                    )
                }
            }
        }
    }
}