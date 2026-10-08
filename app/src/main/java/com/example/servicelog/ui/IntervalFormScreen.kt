package com.example.servicelog.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.servicelog.R
import com.example.servicelog.domain.Category
import com.example.servicelog.domain.MaintenanceInterval
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntervalFormScreen(
    intervalToEdit: MaintenanceInterval?,
    vehicleId: Long,
    onSave: (MaintenanceInterval) -> Unit,
    onCancel: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var title by rememberSaveable(intervalToEdit) {
        mutableStateOf(intervalToEdit?.title ?: "")
    }
    var category by rememberSaveable(intervalToEdit) {
        mutableStateOf(intervalToEdit?.category ?: Category.OTHER)
    }
    var months by rememberSaveable(intervalToEdit) {
        mutableStateOf(intervalToEdit?.intervalMonths?.toString() ?: "")
    }
    var km by rememberSaveable(intervalToEdit) {
        mutableStateOf(intervalToEdit?.intervalKm?.toString() ?: "")
    }
    var refDate by rememberSaveable(intervalToEdit) {
        mutableStateOf(intervalToEdit?.referenceDate)
    }
    var refMileage by rememberSaveable(intervalToEdit) {
        mutableStateOf(intervalToEdit?.referenceMileage?.toString() ?: "")
    }

    var titleError by remember { mutableStateOf(false) }
    var intervalError by remember { mutableStateOf(false) }
    var showCategorySheet by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .imePadding()
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = if (intervalToEdit == null) stringResource(R.string.new_interval) else stringResource(R.string.edit_interval),
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it; titleError = false },
                label = { Text(stringResource(R.string.what_needs_doing)) },
                placeholder = { Text(stringResource(R.string.interval_title_placeholder)) },
                isError = titleError,
                supportingText = { if (titleError) Text(stringResource(R.string.field_required)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = category.label,
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    label = { Text(stringResource(R.string.category_label)) },
                    trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = null) },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { showCategorySheet = true }
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(stringResource(R.string.how_often), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = months,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() }) { months = input; intervalError = false }
                    },
                    label = { Text(stringResource(R.string.months_label)) },
                    placeholder = { Text(stringResource(R.string.months_placeholder)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = intervalError,
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(16.dp))
                OutlinedTextField(
                    value = km,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() }) { km = input; intervalError = false }
                    },
                    label = { Text(stringResource(R.string.kilometres_label)) },
                    placeholder = { Text(stringResource(R.string.km_placeholder)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = intervalError,
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = if (intervalError) stringResource(R.string.set_interval_error)
                else stringResource(R.string.set_interval_hint),
                style = MaterialTheme.typography.bodySmall,
                color = if (intervalError) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )

            Spacer(Modifier.height(24.dp))

            Text(stringResource(R.string.last_done), style = MaterialTheme.typography.titleMedium)
            Text(
                stringResource(R.string.last_done_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = refDate?.format(dayFormatter) ?: "",
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
                value = refMileage,
                onValueChange = { input ->
                    if (input.all { it.isDigit() }) refMileage = input
                },
                label = { Text(stringResource(R.string.mileage_km_label)) },
                placeholder = { Text(stringResource(R.string.mileage_placeholder)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(32.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.cancel))
                }
                Spacer(Modifier.width(16.dp))
                Button(
                    onClick = {
                        val monthsNum = months.toIntOrNull()
                        val kmNum = km.toIntOrNull()

                        titleError = title.isBlank()
                        intervalError = monthsNum == null && kmNum == null

                        if (!titleError && !intervalError) {
                            onSave(
                                MaintenanceInterval(
                                    id = intervalToEdit?.id ?: 0,
                                    vehicleId = vehicleId,
                                    title = title.trim(),
                                    category = category,
                                    intervalMonths = monthsNum,
                                    intervalKm = kmNum,
                                    referenceDate = refDate,
                                    referenceMileage = refMileage.toIntOrNull()
                                )
                            )
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) { Text(stringResource(R.string.save)) }
            }

            if (intervalToEdit != null && onDelete != null) {
                Spacer(Modifier.height(16.dp))
                TextButton(
                    onClick = { showDeleteDialog = true },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) { Text(stringResource(R.string.delete_interval)) }
            }

            Spacer(Modifier.height(24.dp))
        }
    }

    if (showCategorySheet) {
        ModalBottomSheet(
            onDismissRequest = { showCategorySheet = false },
            sheetState = sheetState
        ) {
            Category.entries.forEach { cat ->
                ListItem(
                    headlineContent = { Text(cat.label) },
                    modifier = Modifier.clickable {
                        category = cat
                        showCategorySheet = false
                    }
                )
            }
        }
    }

    if (showDatePicker) {
        val state = rememberDatePickerState(
            initialSelectedDateMillis = refDate?.toEpochDay()?.times(86_400_000)
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let {
                        refDate = LocalDate.ofEpochDay(it / 86_400_000)
                    }
                    showDatePicker = false
                }) { Text(stringResource(R.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text(stringResource(R.string.cancel))}

            }
        ) { DatePicker(state = state) }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {Text(stringResource(R.string.delete_interval_title))}
            ,
            text = { Text(stringResource(R.string.cannot_be_undone))
            },
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