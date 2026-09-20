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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.servicelog.domain.Vehicle
import com.example.servicelog.domain.VehicleType
import com.example.servicelog.domain.icon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(onSaveClick: (Vehicle) -> Unit, onCancelClick: () -> Unit, vehicleToEdit: Vehicle?) {
    var alias by rememberSaveable(vehicleToEdit) { mutableStateOf(vehicleToEdit?.alias ?: "") }
    var brand by rememberSaveable(vehicleToEdit) { mutableStateOf(vehicleToEdit?.brand ?: "") }
    var model by rememberSaveable(vehicleToEdit) { mutableStateOf(vehicleToEdit?.model ?: "") }
    var year by rememberSaveable(vehicleToEdit) { mutableStateOf(vehicleToEdit?.year?.toString() ?: "") }
    var typeOfFuel by rememberSaveable(vehicleToEdit) { mutableStateOf(vehicleToEdit?.typeOfFuel ?: "") }
    var tankCapacity by rememberSaveable(vehicleToEdit) { mutableStateOf(vehicleToEdit?.tankCapacity?.toString() ?: "") }
    var currentKm by rememberSaveable(vehicleToEdit) { mutableStateOf(vehicleToEdit?.currentKm?.toString() ?: "") }

    var fuelMenuExpanded by remember { mutableStateOf(false) }
    val fuelOptions = listOf("Diesel", "Gasoline", "LPG", "Electric", "Hybrid")

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
                        text = "About your ${selectedType.label}",
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
                        label = { Text("Vehicle Type *") },
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
                    label = { Text("Alias") },
                    placeholder = { Text("e.g. Daily Driver, Family SUV") },
                    colors = customTextFieldColors,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = brand,
                    onValueChange = { brand = it; brandError = false },
                    label = { Text("Brand *") },
                    placeholder = { Text("e.g. VW, Toyota, BMW") },
                    isError = brandError,
                    supportingText = {
                        if (brandError) {
                            Text("This field is required", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    colors = customTextFieldColors,
                    modifier = Modifier.fillMaxWidth()

                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = model,
                    onValueChange = { model = it; modelError = false },
                    label = { Text("Model *") },
                    placeholder = { Text("e.g. Fiesta, Golf") },
                    isError = modelError,
                    supportingText = {
                        if (modelError) {
                            Text("This field is required", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    colors = customTextFieldColors,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = year,
                    onValueChange = { year = it; yearError = false },
                    label = { Text("Year of fabrication") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = yearError,
                    supportingText = { if (yearError) Text("Only numbers in this field") },
                    placeholder = { Text("e.g. 2016") },
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
                        value = typeOfFuel,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Type of fuel") },
                        placeholder = { Text("Select fuel type") },
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
                        fuelOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    typeOfFuel = option
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
                    label = { Text("Tank Capacity (Liters)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = tankCapacityError,
                    supportingText = { if (tankCapacityError) Text("Solo se permiten números") },
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
                    label = { Text("Current Km *") },
                    placeholder = { Text("e.g. 375529") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = currentKmError,
                    supportingText = { if (currentKmError) Text("Only numbers in this field") },
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
                        Text("Cancel", color = Color.Black)
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
                                        typeOfFuel = typeOfFuel,
                                        tankCapacity = tankNum,
                                        currentKm = kmNum ?: 0,
                                    )
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEBE0FF))
                    ) {
                        Text("Save", color = Color.Black)
                    }
                }

                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}
