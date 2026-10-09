package com.example.servicelog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.servicelog.ui.FormScreen
import com.example.servicelog.ui.VehicleViewModel
import com.example.servicelog.ui.Navigation
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.*
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.servicelog.domain.calculateDue
import com.example.servicelog.ui.HistoryScreen
import com.example.servicelog.ui.HomeScreen
import com.example.servicelog.ui.IntervalFormScreen
import com.example.servicelog.ui.IntervalViewModel
import com.example.servicelog.ui.IntervalsScreen
import com.example.servicelog.ui.MaintenanceFormScreen
import com.example.servicelog.ui.MaintenanceViewModel
import com.example.servicelog.ui.RefuelFormScreen
import com.example.servicelog.ui.RefuelViewModel
import com.example.servicelog.ui.UiState
import com.example.servicelog.ui.UpdateMileageDialog
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.servicelog.ui.costFormat
import com.example.servicelog.ui.dayFormatter
import com.example.servicelog.ui.moneyFormat
import com.example.servicelog.ui.numberFormat

@AndroidEntryPoint
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
fun AppNavigation(
    viewModel: VehicleViewModel = hiltViewModel(),
    maintenanceViewModel: MaintenanceViewModel = hiltViewModel(),
    intervalViewModel: IntervalViewModel = hiltViewModel(),
    refuelViewModel: RefuelViewModel = hiltViewModel()
) {
    val refuelsState by refuelViewModel.refuels.collectAsStateWithLifecycle()
    val refuels = (refuelsState as? UiState.Content)?.data ?: emptyList()
    val navController = rememberNavController()
    val vehicleState by viewModel.vehicle.collectAsStateWithLifecycle()
    val entriesState by maintenanceViewModel.entries.collectAsStateWithLifecycle()
    val intervalsState by intervalViewModel.intervals.collectAsStateWithLifecycle()

    val vehicle = (vehicleState as? UiState.Content)?.data
    val entries = (entriesState as? UiState.Content)?.data ?: emptyList()
    val intervals = (intervalsState as? UiState.Content)?.data ?: emptyList()

    LaunchedEffect(vehicle?.id) {
        vehicle?.id?.let {
            maintenanceViewModel.setVehicle(it)
            intervalViewModel.setVehicle(it)
            refuelViewModel.setVehicle(it)
        }
    }

    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val showBar = Navigation.entries.any { it.route == currentRoute }

    val dueStatuses = remember(intervals, entries, vehicle) {
        intervals
            .map { calculateDue(it, entries, LocalDate.now(), vehicle?.currentKm) }
            .sortedBy { it.urgency.ordinal }
    }



    var showMileageDialog by remember { mutableStateOf(false) }

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
        },

        floatingActionButton = {
            when (currentRoute) {
                Navigation.HOME.route -> {
                    if (vehicle != null) {
                        FloatingActionButton(onClick = { navController.navigate("maintenanceForm") }) {
                            Icon(Icons.Filled.Add, contentDescription = "Add entry")
                        }
                    }
                }
                Navigation.INTERVALS.route -> {
                    if (vehicle != null) {
                        FloatingActionButton(onClick = { navController.navigate("intervalForm") }) {
                            Icon(Icons.Filled.Add, contentDescription = "Add interval")
                        }
                    }
                }
                Navigation.COSTS.route ->{
                    if (vehicle != null) {
                        FloatingActionButton(onClick = { navController.navigate("refuelForm") }) {
                            Icon(Icons.Filled.Add, contentDescription = "Add refuel")
                        }
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
                    vehicleState = vehicleState,
                    dueStatuses = dueStatuses,
                    onAddClick = { navController.navigate("vehicleForm") },
                    onEditClick = { navController.navigate("vehicleForm") },
                    onUpdateMileage = { showMileageDialog = true }

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

            composable(Navigation.HISTORY.route) {
                HistoryScreen(
                    entries = entries,
                    onEntryClick = { navController.navigate("maintenanceForm/${it.id}") }
                )
            }

            composable("maintenanceForm") {
                MaintenanceFormScreen(
                    entryToEdit = null,
                    vehicleId = vehicle?.id ?: 0,
                    onSave = { entry ->
                        maintenanceViewModel.save(entry)
                        val km = entry.mileage
                        val vehicle = vehicle
                        if (km != null && vehicle != null && km > vehicle.currentKm) {
                            viewModel.save(vehicle.copy(currentKm = km))
                        }
                        navController.popBackStack()
                    },
                    onCancel = { navController.popBackStack() }
                )
            }

            composable(
                route = "maintenanceForm/{entryId}",
                arguments = listOf(navArgument("entryId") { type = NavType.LongType })
            ) { backStackEntry ->
                val entryId = backStackEntry.arguments?.getLong("entryId") ?: 0L
                val entry = entries.find { it.id == entryId }
                MaintenanceFormScreen(
                    entryToEdit = entry,
                    vehicleId = vehicle?.id ?: 0,
                    onSave = { entry ->
                        maintenanceViewModel.save(entry)
                        val km = entry.mileage
                        val vehicle = vehicle
                        if (km != null && vehicle != null && km > vehicle.currentKm) {
                            viewModel.save(vehicle.copy(currentKm = km))
                        }
                        navController.popBackStack()
                    },
                    onCancel = { navController.popBackStack() },
                    onDelete = {
                        entry?.let { maintenanceViewModel.delete(it) }
                        navController.popBackStack()
                    }                )
            }

            composable(Navigation.INTERVALS.route) {
                IntervalsScreen(
                    intervals = intervals,
                    onIntervalClick = { navController.navigate("intervalForm/${it.id}") },
                    onAddClick = { navController.navigate("intervalForm") }
                )
            }

            composable("intervalForm") {
                IntervalFormScreen(
                    intervalToEdit = null,
                    vehicleId = vehicle?.id ?: 0,
                    onSave = {
                        intervalViewModel.save(it)
                        navController.popBackStack()
                    },
                    onCancel = { navController.popBackStack() }
                )
            }

            composable(
                route = "intervalForm/{intervalId}",
                arguments = listOf(navArgument("intervalId") { type = NavType.LongType })
            ) { backStackEntry ->
                val intervalId = backStackEntry.arguments?.getLong("intervalId") ?: 0L
                val interval = intervals.find { it.id == intervalId }
                IntervalFormScreen(
                    intervalToEdit = interval,
                    vehicleId = vehicle?.id ?: 0,
                    onSave = {
                        intervalViewModel.save(it)
                        navController.popBackStack()
                    },
                    onCancel = { navController.popBackStack() },
                    onDelete = {
                        interval?.let { intervalViewModel.delete(it) }
                        navController.popBackStack()
                    }
                )
            }

            composable("refuelForm") {
                RefuelFormScreen(
                    refuelToEdit = null,
                    vehicleId = vehicle?.id ?: 0,
                    lastMileage = vehicle?.currentKm ?: 0,
                    tankCapacity = vehicle?.tankCapacity,
                    onSave = {
                        refuelViewModel.save(it)
                        val vehicle = vehicle
                        if (vehicle != null && it.mileage > vehicle.currentKm) {
                            viewModel.save(vehicle.copy(currentKm = it.mileage, lastReadingDate = it.date))
                        }
                        navController.popBackStack()
                    },
                    onCancel = { navController.popBackStack() }
                )
            }

            composable(
                route = "refuelForm/{refuelId}",
                arguments = listOf(navArgument("refuelId") { type = NavType.LongType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("refuelId") ?: 0L
                val refuel = refuels.find { it.id == id }
                RefuelFormScreen(
                    refuelToEdit = refuel,
                    vehicleId = vehicle?.id ?: 0,
                    lastMileage = vehicle?.currentKm ?: 0,
                    tankCapacity = vehicle?.tankCapacity,
                    onSave = { refuelViewModel.save(it); navController.popBackStack() },
                    onCancel = { navController.popBackStack() },
                    onDelete = {
                        refuel?.let { refuelViewModel.delete(it) }
                        navController.popBackStack()
                    }
                )
            }

            composable(Navigation.COSTS.route) {
                LazyColumn(Modifier.fillMaxSize()) {
                    items(refuels.sortedByDescending { it.mileage }) { r ->
                        ListItem(
                            headlineContent = {
                                Text("${numberFormat.format(r.mileage)} km · ${costFormat.format(r.liters)} L")
                            },
                            supportingContent = { Text(r.date.format(dayFormatter)) },
                            trailingContent = { Text(moneyFormat.format(r.costCents / 100.0)) },
                            modifier = Modifier.clickable { navController.navigate("refuelForm/${r.id}") }
                        )
                        HorizontalDivider()
                    }
                }
            }        }
    }

    val v = vehicle
    if (showMileageDialog && v != null) {
        UpdateMileageDialog(
            currentKm = v.currentKm,
            onConfirm = { km ->
                viewModel.save(v.copy(currentKm = km, lastReadingDate = LocalDate.now()))
                showMileageDialog = false
            },
            onDismiss = { showMileageDialog = false }
        )
    }
}

@Composable
fun PlaceholderScreen(name: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(name, style = MaterialTheme.typography.headlineMedium)
    }
}

