package com.example.servicelog.ui

import android.content.res.Configuration
import android.icu.text.NumberFormat
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.servicelog.domain.Category
import com.example.servicelog.domain.MaintenanceEntry
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.runtime.CompositionLocalProvider

private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceFormScreen(
    entryToEdit: MaintenanceEntry?,
    vehicleId: Long,
    onSave: (MaintenanceEntry) -> Unit,
    onCancel: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var title by rememberSaveable(entryToEdit) { mutableStateOf(entryToEdit?.title ?: "") }
    var category by rememberSaveable(entryToEdit) { mutableStateOf(entryToEdit?.category ?: Category.OTHER) }
    var date by rememberSaveable(entryToEdit) { mutableStateOf(entryToEdit?.date) }
    var dateApprox by rememberSaveable(entryToEdit) { mutableStateOf(entryToEdit?.dateIsApproximate ?: false) }
    var mileage by rememberSaveable(entryToEdit) { mutableStateOf(entryToEdit?.mileage?.toString() ?: "") }

    val locale = LocalConfiguration.current.locales[0]
    val costFormat = remember(locale) {
        NumberFormat.getNumberInstance(locale).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
            isGroupingUsed = false
        }
    }

    var cost by rememberSaveable(entryToEdit) {
        mutableStateOf(entryToEdit?.costCents?.let { costFormat.format(it / 100.0) } ?: "")
    }

    var workshop by rememberSaveable(entryToEdit) { mutableStateOf(entryToEdit?.workshop ?: "") }
    var notes by rememberSaveable(entryToEdit) { mutableStateOf(entryToEdit?.notes ?: "") }

    var titleError by remember { mutableStateOf(false) }
    var dateOrMileageError by remember { mutableStateOf(false) }
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
                text = if (entryToEdit == null) "New entry" else "Edit entry",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it; titleError = false },
                label = { Text("What was done? *") },
                placeholder = { Text("e.g. Oil + filter change (10w40)") },
                isError = titleError,
                supportingText = { if (titleError) Text("This field is required") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
        Box(modifier = Modifier.fillMaxWidth()){
            OutlinedTextField(
                value = category.label,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                label = { Text("Category") },
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
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = date?.format(dateFormatter) ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Date") },
                isError = dateOrMileageError,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Filled.DateRange, contentDescription = "Pick date")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = dateApprox, onCheckedChange = { dateApprox = it })
                Text("Approximate date")
            }

            OutlinedTextField(
                value = mileage,
                onValueChange = { input ->
                    if (input.all { it.isDigit() }) { mileage = input; dateOrMileageError = false }
                },
                label = { Text("Mileage (km)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = dateOrMileageError,
                supportingText = {
                    Text(if (dateOrMileageError) "Enter a date or a mileage" else "Date or mileage required")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = cost,
                onValueChange = { cost = it },
                label = { Text("Cost (€)") },
                placeholder = { Text("e.g. 89.50") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = workshop,
                onValueChange = { workshop = it },
                label = { Text("Workshop") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            if (entryToEdit != null && onDelete != null) {
                TextButton(
                    onClick = { showDeleteDialog = true },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Delete entry")
                }
            }

            Spacer(Modifier.height(32.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) {
                    Text("Cancel")
                }
                Spacer(Modifier.width(16.dp))
                Button(
                    onClick = {
                        val mileageNum = mileage.toIntOrNull()
                        val costCents = cost.replace(",", ".").toDoubleOrNull()
                            ?.let { (it * 100).toInt() }

                        titleError = title.isBlank()
                        dateOrMileageError = date == null && mileageNum == null

                        if (!titleError && !dateOrMileageError) {
                            onSave(
                                MaintenanceEntry(
                                    id = entryToEdit?.id ?: 0,
                                    vehicleId = vehicleId,
                                    title = title.trim(),
                                    category = category,
                                    date = date,
                                    dateIsApproximate = dateApprox,
                                    mileage = mileageNum,
                                    costCents = costCents,
                                    workshop = workshop.ifBlank { null },
                                    notes = notes.ifBlank { null },
                                    photoUri = null
                                )
                            )
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Save") }
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
            initialSelectedDateMillis = date?.toEpochDay()?.times(86_400_000)
        )
        val config = Configuration(LocalConfiguration.current).apply {
            setLocale(appLocale)
        }
        CompositionLocalProvider(LocalConfiguration provides config) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        state.selectedDateMillis?.let {
                            date = LocalDate.ofEpochDay(it / 86_400_000)
                        }
                        showDatePicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                }
            ) { DatePicker(state = state) }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete this entry?") },
            text = { Text("This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = { showDeleteDialog = false; onDelete?.invoke() },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }
}
