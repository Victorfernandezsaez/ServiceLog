package com.example.servicelog.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.servicelog.R
import com.example.servicelog.domain.FuelType
import com.example.servicelog.domain.Vehicle
import com.example.servicelog.domain.VehicleType
import com.example.servicelog.domain.icon
import com.example.servicelog.ui.viewModels.VehicleFormViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    onDone: () -> Unit,
    vm: VehicleFormViewModel = hiltViewModel()
) {
    val vehicleToEdit by vm.vehicle.collectAsStateWithLifecycle()

    var alias by rememberSaveable(vehicleToEdit) { mutableStateOf(vehicleToEdit?.alias ?: "") }
    var brand by rememberSaveable(vehicleToEdit) { mutableStateOf(vehicleToEdit?.brand ?: "") }
    var model by rememberSaveable(vehicleToEdit) { mutableStateOf(vehicleToEdit?.model ?: "") }
    var year by rememberSaveable(vehicleToEdit) {
        mutableStateOf(vehicleToEdit?.year?.toString() ?: "")
    }
    var selectedFuelType by rememberSaveable(vehicleToEdit) {
        mutableStateOf(vehicleToEdit?.fuelType)
    }
    var tankCapacity by rememberSaveable(vehicleToEdit) {
        mutableStateOf(vehicleToEdit?.tankCapacity?.toString() ?: "")
    }
    var currentKm by rememberSaveable { mutableStateOf("") }
    var selectedType by rememberSaveable(vehicleToEdit) {
        mutableStateOf(vehicleToEdit?.type ?: VehicleType.CAR)
    }

    var fuelMenuExpanded by remember { mutableStateOf(false) }
    var vehicleMenuExpanded by remember { mutableStateOf(false) }

    var brandError by remember { mutableStateOf(false) }
    var modelError by remember { mutableStateOf(false) }
    var yearError by remember { mutableStateOf(false) }
    var tankCapacityError by remember { mutableStateOf(false) }

    val customTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        errorContainerColor = MaterialTheme.colorScheme.errorContainer
    )

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                IconButton(
                    onClick = onDone,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.cancel),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 70.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = selectedType.icon,
                        contentDescription = selectedType.label,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.about_your_vehicle, selectedType.label),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                ExposedDropdownMenuBox(
                    expanded = vehicleMenuExpanded,
                    onExpandedChange = { vehicleMenuExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedType.label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.vehicle_type_label)) },
                        colors = customTextFieldColors,
                        leadingIcon = { Icon(selectedType.icon, contentDescription = null) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = vehicleMenuExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(
                                type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                                enabled = true
                            )
                    )

                    ExposedDropdownMenu(
                        expanded = vehicleMenuExpanded,
                        onDismissRequest = { vehicleMenuExpanded = false }
                    ) {
                        VehicleType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.label) },
                                leadingIcon = { Icon(type.icon, contentDescription = type.label) },
                                onClick = { selectedType = type; vehicleMenuExpanded = false }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = alias,
                    onValueChange = { alias = it },
                    label = { Text(stringResource(R.string.alias_label)) },
                    placeholder = { Text(stringResource(R.string.alias_placeholder)) },
                    colors = customTextFieldColors,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = brand,
                    onValueChange = { brand = it; brandError = false },
                    label = { Text(stringResource(R.string.brand_label)) },
                    placeholder = { Text(stringResource(R.string.brand_placeholder)) },
                    isError = brandError,
                    supportingText = {
                        if (brandError) {
                            Text(
                                stringResource(R.string.field_required),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    colors = customTextFieldColors,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = model,
                    onValueChange = { model = it; modelError = false },
                    label = { Text(stringResource(R.string.model_label)) },
                    placeholder = { Text(stringResource(R.string.model_placeholder)) },
                    isError = modelError,
                    supportingText = {
                        if (modelError) {
                            Text(
                                stringResource(R.string.field_required),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    colors = customTextFieldColors,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = year,
                    onValueChange = { year = it; yearError = false },
                    label = { Text(stringResource(R.string.year_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = yearError,
                    supportingText = { if (yearError) Text(stringResource(R.string.only_numbers_error)) },
                    placeholder = { Text(stringResource(R.string.year_placeholder)) },
                    colors = customTextFieldColors,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                ExposedDropdownMenuBox(
                    expanded = fuelMenuExpanded,
                    onExpandedChange = { fuelMenuExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedFuelType?.let { stringResource(it.labelRes) } ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.fuel_type_label)) },
                        placeholder = { Text(stringResource(R.string.fuel_type_placeholder)) },
                        colors = customTextFieldColors,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = fuelMenuExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(
                                type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                                enabled = true
                            )
                    )

                    ExposedDropdownMenu(
                        expanded = fuelMenuExpanded,
                        onDismissRequest = { fuelMenuExpanded = false }
                    ) {
                        FuelType.entries.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(stringResource(option.labelRes)) },
                                onClick = {
                                    selectedFuelType = option
                                    fuelMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = tankCapacity,
                    onValueChange = { tankCapacity = it; tankCapacityError = false },
                    label = { Text(stringResource(R.string.tank_capacity_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = tankCapacityError,
                    suffix = { Text("L") },
                    supportingText = { if (tankCapacityError) Text(stringResource(R.string.only_numbers_error)) },
                    colors = customTextFieldColors,
                    modifier = Modifier.fillMaxWidth()
                )

                if (vehicleToEdit == null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = currentKm,
                        onValueChange = { input ->
                            if (input.all { it.isDigit() }) currentKm = input
                        },
                        label = { Text(stringResource(R.string.current_km_label)) },
                        placeholder = { Text(stringResource(R.string.current_km_placeholder)) },
                        suffix = { Text("km") },
                        supportingText = { Text(stringResource(R.string.current_km_hint)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = customTextFieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDone,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.cancel))
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = {
                            brandError = false
                            modelError = false
                            yearError = false
                            tankCapacityError = false

                            var hasValidationError = false

                            if (brand.isBlank()) {
                                brandError = true
                                hasValidationError = true
                            }
                            if (model.isBlank()) {
                                modelError = true
                                hasValidationError = true
                            }

                            val yearNum = year.toIntOrNull()
                            val tankNum = tankCapacity.toIntOrNull()

                            if (year.isNotBlank() && yearNum == null) {
                                yearError = true
                                hasValidationError = true
                            }
                            if (tankCapacity.isNotBlank() && tankNum == null) {
                                tankCapacityError = true
                                hasValidationError = true
                            }

                            if (!hasValidationError) {
                                vm.save(
                                    Vehicle(
                                        id = vehicleToEdit?.id ?: 0,
                                        type = selectedType,
                                        alias = alias,
                                        brand = brand,
                                        model = model,
                                        year = yearNum,
                                        fuelType = selectedFuelType,
                                        tankCapacity = tankNum
                                    ),
                                    initialMileage = currentKm.toIntOrNull()
                                )
                                onDone()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Text(stringResource(R.string.save))
                    }
                }

                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}