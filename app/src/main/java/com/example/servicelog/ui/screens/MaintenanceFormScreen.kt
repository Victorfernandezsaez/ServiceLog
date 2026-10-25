package com.example.servicelog.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.servicelog.R
import com.example.servicelog.domain.Category
import com.example.servicelog.domain.MaintenanceEntry
import com.example.servicelog.ui.DateField
import com.example.servicelog.ui.costFormat
import com.example.servicelog.ui.numberFormat
import com.example.servicelog.ui.theme.warningAmberDark
import com.example.servicelog.ui.theme.warningLight
import com.example.servicelog.ui.viewModels.MaintenanceFormViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceFormScreen(
    onDone: () -> Unit,
    vm: MaintenanceFormViewModel = hiltViewModel()
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val entryToEdit = uiState.entry
    val vehicleId = uiState.vehicleId ?: return // Wait for vehicleId

    var title by rememberSaveable(entryToEdit) { mutableStateOf(entryToEdit?.title ?: "") }
    var category by rememberSaveable(entryToEdit) {
        mutableStateOf(
            entryToEdit?.category ?: Category.OTHER
        )
    }
    var date by rememberSaveable(entryToEdit) { mutableStateOf(entryToEdit?.date) }
    var dateApprox by rememberSaveable(entryToEdit) {
        mutableStateOf(
            entryToEdit?.dateIsApproximate ?: false
        )
    }
    var mileage by rememberSaveable(entryToEdit) {
        mutableStateOf(
            entryToEdit?.mileage?.toString() ?: ""
        )
    }


    var cost by rememberSaveable(entryToEdit) {
        mutableStateOf(entryToEdit?.costCents?.let { costFormat.format(it / 100.0) } ?: "")
    }

    var workshop by rememberSaveable(entryToEdit) { mutableStateOf(entryToEdit?.workshop ?: "") }
    var notes by rememberSaveable(entryToEdit) { mutableStateOf(entryToEdit?.notes ?: "") }

    var titleError by remember { mutableStateOf(false) }
    var dateOrMileageError by remember { mutableStateOf(false) }
    var showCategorySheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var mileageWarning by remember { mutableStateOf(false) }

    LaunchedEffect(date, mileage, uiState) {
        val km = mileage.toIntOrNull()
        val currentKm = uiState.currentKm
        val lastDate = uiState.lastReadingDate
        val d = date
        mileageWarning = km != null && currentKm != null && km < currentKm &&
                (lastDate == null || d == null || !d.isBefore(lastDate))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(
                            if (entryToEdit == null) R.string.new_entry else R.string.edit_entry
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = stringResource(R.string.cancel)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .imePadding()
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it; titleError = false },
                label = { Text(stringResource(R.string.what_was_done)) },
                placeholder = { Text(stringResource(R.string.entry_title_placeholder)) },
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
            Spacer(Modifier.height(16.dp))

            DateField(date = date, onDateChange = { date = it })

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = dateApprox, onCheckedChange = { dateApprox = it })
                Text(stringResource(R.string.approximate_date))
            }

            OutlinedTextField(
                value = mileage,
                onValueChange = { input ->
                    if (input.all { it.isDigit() }) {
                        mileage = input; dateOrMileageError = false
                    }
                },
                label = { Text(stringResource(R.string.mileage_km_label)) },
                suffix = { Text(stringResource(R.string.km_suffix)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = dateOrMileageError,
                supportingText = {
                    Column {
                        if (dateOrMileageError) {
                            Text(stringResource(R.string.date_or_mileage_error))
                        } else {
                            Text(stringResource(R.string.date_or_mileage_required))
                        }
                        if (mileageWarning) {
                            Text(
                                stringResource(
                                    R.string.mileage_lower_warning,
                                    numberFormat.format(uiState.currentKm ?: 0)
                                ),
                                color = if (isSystemInDarkTheme()) warningAmberDark else warningLight
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = cost,
                onValueChange = { cost = it },
                label = { Text(stringResource(R.string.cost_label)) },
                placeholder = { Text(stringResource(R.string.cost_placeholder)) },
                suffix = { Text(stringResource(R.string.eur_suffix)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = workshop,
                onValueChange = { workshop = it },
                label = { Text(stringResource(R.string.workshop_label)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text(stringResource(R.string.notes_label)) },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(32.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = onDone, modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.cancel))
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
                            vm.save(
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
                            onDone()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) { Text(stringResource(R.string.save)) }
            }

            if (entryToEdit != null) {
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
                    Text(stringResource(R.string.delete_entry))
                }
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

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(stringResource(R.string.delete_entry_title))
            },
            text = { Text(stringResource(R.string.cannot_be_undone)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        entryToEdit?.let { vm.delete(it) }
                        onDone()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) { Text(stringResource(R.string.delete)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}
