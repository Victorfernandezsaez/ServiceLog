package com.example.servicelog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.servicelog.ui.FormScreen
import com.example.servicelog.ui.VehicleViewModel
import com.example.servicelog.ui.Navigation
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.*
import androidx.compose.runtime.getValue
import com.example.servicelog.ui.HomeScreen

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
    val navController = rememberNavController()
    val vehicle by viewModel.vehicle.collectAsStateWithLifecycle()

    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val showBar = Navigation.entries.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBar) {
                NavigationBar {
                    Navigation.entries.forEach { dest ->
                        NavigationBarItem(
                            selected = currentRoute == dest.route,
                            onClick = {
                                navController.navigate(dest.route) {
                                    popUpTo(Navigation.HOME.route)
                                    launchSingleTop = true
                                }
                            },
                            icon = { Icon(dest.icon, contentDescription = dest.label) },
                            label = { Text(dest.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Navigation.HOME.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Navigation.HOME.route) {
                HomeScreen(
                    vehicle = vehicle,
                    onAddClick = { navController.navigate("vehicleForm") },
                    onEditClick = { navController.navigate("vehicleForm") }
                )
            }
            composable("vehicleForm") {
                FormScreen(
                    vehicleToEdit = vehicle,
                    onSaveClick = {
                        viewModel.save(it)
                        navController.popBackStack()
                    },
                    onCancelClick = { navController.popBackStack() }
                )
            }
            composable(Navigation.HISTORY.route) { PlaceholderScreen("History") }
            composable(Navigation.INTERVALS.route) { PlaceholderScreen("Intervals") }
            composable(Navigation.COSTS.route) { PlaceholderScreen("Costs") }
        }
    }
}

@Composable
fun PlaceholderScreen(name: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(name, style = MaterialTheme.typography.headlineMedium)
    }
}

