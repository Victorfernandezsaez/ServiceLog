package com.example.servicelog.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.servicelog.domain.Vehicle
import com.example.servicelog.domain.VehicleType
import com.example.servicelog.domain.FuelType
import com.example.servicelog.domain.icon
import androidx.compose.ui.res.pluralStringResource
import com.example.servicelog.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(onSaveClick: (Vehicle) -> Unit, onCancelClick: () -> Unit, vehicleToEdit: Vehicle?) {
    var alias by rememberSaveable(vehicleToEdit) { mutableStateOf(vehicleToEdit?.alias ?: "") }
    var brand by rememberSaveable(vehicleToEdit) { mutableStateOf(vehicleToEdit?.brand ?: "") }
    var model by rememberSaveable(vehicleToEdit) { mutableStateOf(vehicleToEdit?.model ?: "") }
    var year by rememberSaveable(vehicleToEdit) { mutableStateOf(vehicleToEdit?.year?.toString() ?: "") }
    var selectedFuelType by rememberSaveable(vehicleToEdit) { mutableStateOf(vehicleToEdit?.fuelType) }
    var tankCapacity by rememberSaveable(vehicleToEdit) { mutableStateOf(vehicleToEdit?.tankCapacity?.toString() ?: "") }
    var currentKm by rememberSaveable(vehicleToEdit) { mutableStateOf(vehicleToEdit?.currentKm?.toString() ?: "") }

    var fuelMenuExpanded by remember { mutableStateOf(false) }

    var brandError by remember { mutableStateOf(false) }
    var modelError by remember { mutableStateOf(false) }
    var yearError by remember { mutableStateOf(false) }
    var tankCapacityError by remember { mutableStateOf(false) }
    var currentKmError by remember { mutableStateOf(false) }

    var vehicleMenuExpanded by remember { mutableStateOf(false) }
    var selectedType by rememberSaveable(vehicleToEdit) {
        mutableStateOf(vehicleToEdit?.type ?: VehicleType.CAR)
    }
    val customTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color(0xFFF3EDFF),
        unfocusedContainerColor = Color(0xFFF3EDFF),
        errorContainerColor = Color(0xFFFFE6E6)
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
                    .background(Color(0xFFEBE0FF), shape = RoundedCornerShape(16.dp))
                    .padding(vertical = 70.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = selectedType.icon,
                        contentDescription = selectedType.label,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.about_your_vehicle, selectedType.label),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
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
                        leadingIcon = {
                            Icon(selectedType.icon, contentDescription = null)
                        },
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
                            Text(stringResource(R.string.field_required), color = MaterialTheme.colorScheme.error)
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
                            Text(stringResource(R.string.field_required), color = MaterialTheme.colorScheme.error)
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

                // 3. Select para el Tipo de Combustible
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
                    supportingText = { if (tankCapacityError) Text(stringResource(R.string.only_numbers)) },
                    colors = customTextFieldColors,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = currentKm,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() }) {
                            currentKm = input; currentKmError = false
                        }
                    },
                    label = { Text(stringResource(R.string.current_km_label)) },
                    placeholder = { Text(stringResource(R.string.current_km_placeholder)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = currentKmError,
                    supportingText = { if (currentKmError) Text(stringResource(R.string.only_numbers_error)) },
                    colors = customTextFieldColors,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { onCancelClick() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.cancel), color = Color.Black)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = {
                            brandError = false
                            modelError = false
                            yearError = false
                            tankCapacityError = false
                            currentKmError = false

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
                            val kmNum = currentKm.toIntOrNull()

                            if (year.isNotBlank() && yearNum == null) {
                                yearError = true
                                hasValidationError = true
                            }
                            if (tankCapacity.isNotBlank() && tankNum == null) {
                                tankCapacityError = true
                                hasValidationError = true
                            }
                            if (currentKm.isBlank() || kmNum == null) {
                                currentKmError = true
                                hasValidationError = true
                            }

                            if (!hasValidationError) {
                                onSaveClick(
                                    Vehicle(
                                        id = vehicleToEdit?.id ?: 0,
                                        type = selectedType,
                                        alias = alias,
                                        brand = brand,
                                        model = model,
                                        year = yearNum,
                                        fuelType = selectedFuelType,
                                        tankCapacity = tankNum,
                                        currentKm = kmNum ?: 0,
                                    )
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEBE0FF))
                    ) {
                        Text(stringResource(R.string.save), color = Color.Black)
                    }
                }

                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}
