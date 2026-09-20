package com.example.servicelog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.servicelog.domain.Vehicle
import com.example.servicelog.ui.FormScreen
import com.example.servicelog.ui.VehicleInfo
import com.example.servicelog.ui.VehicleViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: VehicleViewModel = viewModel()) {
    var currentScreen by rememberSaveable() { mutableStateOf("Home") }
    val vehicle by viewModel.vehicle.collectAsStateWithLifecycle()

    if (currentScreen == "Home") {
        HomeScreen(
            vehicle = vehicle,
            onAddClick = { currentScreen = "Form" },
            onEditClick = { currentScreen = "Form" }
        )
    } else {
        FormScreen(
            vehicleToEdit = vehicle,
            onSaveClick = {
                viewModel.save(it)
                currentScreen = "Home"
            },
            onCancelClick = { currentScreen = "Home" }
        )
    }
}

@Composable
fun HomeScreen(vehicle: Vehicle?, onAddClick: () -> Unit, onEditClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(50.dp))

        if (vehicle == null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clickable { onAddClick() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEBE0FF))
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.DirectionsCar, contentDescription = "Add", modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "Add", fontSize = 24.sp)
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onEditClick() }
            ) {
                VehicleInfo(vehicle)
            }
        }
    }
}