package com.example.servicelog.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.servicelog.R
import com.example.servicelog.domain.Refuel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RefuelFormScreen(
    refuelToEdit: Refuel?,
    vehicleId: Long,
    lastMileage: Int,
    tankCapacity: Int?,
    onSave: (Refuel) -> Unit,
    onCancel: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var date by rememberSaveable(refuelToEdit) {
        mutableStateOf(refuelToEdit?.date ?: LocalDate.now())
    }
    var mileage by rememberSaveable(refuelToEdit) {
        mutableStateOf(refuelToEdit?.mileage?.toString() ?: lastMileage.toString())
    }
    var liters by rememberSaveable(refuelToEdit) {
        mutableStateOf(refuelToEdit?.liters?.let { costFormat.format(it) } ?: "")
    }
    var cost by rememberSaveable(refuelToEdit) {
        mutableStateOf(refuelToEdit?.costCents?.let { costFormat.format(it / 100.0) } ?: "")
    }
    var fullTank by rememberSaveable(refuelToEdit) {
        mutableStateOf(refuelToEdit?.fullTank ?: true)
    }

    var mileageError by remember { mutableStateOf<Int?>(null) }
    var litersError by remember { mutableStateOf<Int?>(null) }
    var costError by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .imePadding()
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onCancel, modifier = Modifier.padding(end = 8.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.cancel)
                    )
                }
                Text(
                    text = stringResource(
                        if (refuelToEdit == null) R.string.new_refuel else R.string.edit_refuel
                    ),
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = date.format(dayFormatter),
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.date_label)) },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Filled.DateRange, contentDescription = stringResource(R.string.pick_date_desc))
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = mileage,
                onValueChange = { input ->
                    if (input.all { it.isDigit() }) { mileage = input; mileageError = null }
                },
                label = { Text(stringResource(R.string.mileage_km_label)) },
                suffix = { Text("km") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = mileageError != null,
                supportingText = { mileageError?.let { Text(stringResource(it)) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = liters,
                onValueChange = { liters = it; litersError = null },
                label = { Text(stringResource(R.string.liters_label)) },
                placeholder = { Text(stringResource(R.string.liters_placeholder)) },
                suffix = { Text("L") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = litersError != null,
                supportingText = { litersError?.let { Text(stringResource(it)) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = cost,
                onValueChange = { cost = it; costError = false },
                label = { Text(stringResource(R.string.cost_label)) },
                placeholder = { Text(stringResource(R.string.cost_placeholder)) },
                suffix = { Text("€") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = costError,
                supportingText = { if (costError) Text(stringResource(R.string.error_enter_a_number)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = fullTank, onCheckedChange = { fullTank = it })
                Column {
                    Text(stringResource(R.string.full_tank))
                    Text(
                        stringResource(R.string.full_tank_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.cancel))
                }
                Spacer(Modifier.width(16.dp))
                Button(
                    onClick = {
                        val mileageNum = mileage.toIntOrNull()
                        val litersNum = liters.replace(",", ".").toDoubleOrNull()
                        val costCents = cost.replace(",", ".").toDoubleOrNull()
                            ?.let { (it * 100).toInt() }

                        mileageError = when {
                            mileageNum == null -> R.string.error_enter_a_number
                            refuelToEdit == null && mileageNum < lastMileage ->
                                R.string.error_cannot_be_lower_than_last_reading
                            else -> null
                        }
                        litersError = when {
                            litersNum == null || litersNum <= 0 -> R.string.error_enter_litres
                            tankCapacity != null && litersNum > tankCapacity * 1.1 ->
                                R.string.error_more_than_tank_holds
                            else -> null
                        }
                        costError = costCents == null

                        if (mileageError == null && litersError == null && !costError) {
                            onSave(
                                Refuel(
                                    id = refuelToEdit?.id ?: 0,
                                    vehicleId = vehicleId,
                                    date = date,
                                    mileage = mileageNum!!,
                                    liters = litersNum!!,
                                    costCents = costCents!!,
                                    fullTank = fullTank
                                )
                            )
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) { Text(stringResource(R.string.save)) }
            }

            if (refuelToEdit != null && onDelete != null) {
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { showDeleteDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(stringResource(R.string.delete_refuel))
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }

    if (showDatePicker) {
        val state = rememberDatePickerState(
            initialSelectedDateMillis = date.toEpochDay() * 86_400_000
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { date = LocalDate.ofEpochDay(it / 86_400_000) }
                    showDatePicker = false
                }) { Text(stringResource(R.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text(stringResource(R.string.cancel)) }
            }
        ) { DatePicker(state = state) }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_refuel_title)) },
            text = { Text(stringResource(R.string.cannot_be_undone)) },
            confirmButton = {
                TextButton(
                    onClick = { showDeleteDialog = false; onDelete?.invoke() },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) { Text(stringResource(R.string.delete)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}