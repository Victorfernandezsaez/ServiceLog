package com.example.servicelog.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.servicelog.R

@Composable
fun UpdateMileageDialog(
    currentKm: Int,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var value by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.update_mileage)) },
        text = {
            Column {
                Text(
                    stringResource(R.string.current_mileage_format, numberFormat.format(currentKm)),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = value,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() } && input.length <= 7) {
                            value = input
                            error = null
                        }
                    },
                    label = { Text(stringResource(R.string.new_reading_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = error != null,
                    supportingText = { error?.let { Text(it) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            val enterANumber = stringResource(R.string.enter_a_number)
            val cannotBeLower = stringResource(R.string.mileage_too_low)
            TextButton(onClick = {
                val km = value.toIntOrNull()
                when {
                    km == null -> error = enterANumber
                    km < currentKm -> error = cannotBeLower
                    else -> onConfirm(km)
                }
            }) { Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}