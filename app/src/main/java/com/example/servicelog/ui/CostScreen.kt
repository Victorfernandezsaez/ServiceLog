package com.example.servicelog.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.servicelog.domain.CostSummary
import com.example.servicelog.domain.Refuel
import java.time.YearMonth
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.res.stringResource
import com.example.servicelog.R
import androidx.compose.foundation.lazy.items

    @Composable
    fun CostsScreen(
        summaryState: UiState<CostSummary>,
        onRefuelClick: (Refuel) -> Unit
    ) {
        when (val s = summaryState) {
            is UiState.Loading -> { /* CircularProgressIndicator centrado */ }
            is UiState.Error -> { /* mensaje de error */ }
            is UiState.Content -> {
                val data = s.data
                val currentMonth = YearMonth.now().toString()   // "2026-10"
                val fuel = data.monthlyFuelCents[currentMonth] ?: 0
                val maintenance = data.monthlyMaintenanceCents[currentMonth] ?: 0

                LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
                    item {
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp)) {
                                Text(stringResource(R.string.recent_month),
                                    style = MaterialTheme.typography.labelMedium)
                                Text(moneyFormat.format((fuel + maintenance) / 100.0),
                                    style = MaterialTheme.typography.headlineMedium)
                                Spacer(Modifier.height(12.dp))
                                Row {
                                    CostTile(stringResource(R.string.fuel), fuel, Modifier.weight(1f))
                                    Spacer(Modifier.width(12.dp))
                                    CostTile(stringResource(R.string.maintenance), maintenance, Modifier.weight(1f))
                                }
                            }
                        }
                    }

                    item {
                        Spacer(Modifier.height(16.dp))
                        data.average?.let {
                            Text(
                                stringResource(
                                    R.string.average_consumption,
                                    costFormat.format(it.litersPer100Km)
                                ),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        } ?: Text(
                            stringResource(R.string.not_enough_refuels),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        data.lastStretch?.let {
                            Text(
                                stringResource(
                                    R.string.last_tank_consumption,
                                    costFormat.format(it.litersPer100Km)
                                ),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                    }

                    item {
                        Text(stringResource(R.string.recent_refuels),
                            style = MaterialTheme.typography.titleMedium)
                    }

                    items(data.refuels.sortedByDescending { it.mileage }) { r ->
                        ListItem(
                            headlineContent = {
                                Text(stringResource(R.string.mileage_km_format,
                                    numberFormat.format(r.mileage)))
                            },
                            supportingContent = {
                                Text("${costFormat.format(r.liters)} L · ${r.date.format(dayFormatter)}")
                            },
                            trailingContent = { Text(moneyFormat.format(r.costCents / 100.0)) },
                            modifier = Modifier.clickable { onRefuelClick(r) }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
    @Composable
    private fun CostTile(label: String, cents: Int, modifier: Modifier = Modifier) {
        Card(
            modifier = modifier,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(Modifier.padding(12.dp)) {
                Text(label, style = MaterialTheme.typography.labelSmall)
                Text(
                    moneyFormat.format(cents / 100.0),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
