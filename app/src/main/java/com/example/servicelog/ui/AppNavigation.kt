package com.example.servicelog.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.servicelog.R
import com.example.servicelog.ui.screens.CostsScreen
import com.example.servicelog.ui.screens.FormScreen
import com.example.servicelog.ui.screens.HistoryScreen
import com.example.servicelog.ui.screens.HomeScreen
import com.example.servicelog.ui.screens.IntervalFormScreen
import com.example.servicelog.ui.screens.IntervalsScreen
import com.example.servicelog.ui.screens.MaintenanceFormScreen
import com.example.servicelog.ui.screens.RefuelFormScreen
import com.example.servicelog.ui.uistate.UiState
import com.example.servicelog.ui.viewModels.HomeViewModel
import com.example.servicelog.ui.viewModels.IntervalViewModel
import com.example.servicelog.ui.viewModels.RefuelViewModel
import com.example.servicelog.ui.viewModels.VehicleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    viewModel: VehicleViewModel = hiltViewModel(),
    intervalViewModel: IntervalViewModel = hiltViewModel(),
    refuelViewModel: RefuelViewModel = hiltViewModel()
) {

    NotificationSetup()

    val refuelsState by refuelViewModel.refuels.collectAsStateWithLifecycle()
    val refuels = (refuelsState as? UiState.Content)?.data ?: emptyList()
    val navController = rememberNavController()
    val vehicleState by viewModel.vehicle.collectAsStateWithLifecycle()
    val intervalsState by intervalViewModel.intervals.collectAsStateWithLifecycle()

    val vehicle = (vehicleState as? UiState.Content)?.data
    val intervals = (intervalsState as? UiState.Content)?.data ?: emptyList()

    LaunchedEffect(vehicle?.id) {
        vehicle?.id?.let {
            intervalViewModel.setVehicle(it)
        }
    }

    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val showBar = Navigation.entries.any { it.route == currentRoute }

    val homeVm: HomeViewModel = hiltViewModel()
    val homeState by homeVm.uiState.collectAsStateWithLifecycle()

    var showMileageDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            if (showBar) {
                val titleRes = when (currentRoute) {
                    Navigation.HOME.route -> R.string.app_name
                    Navigation.HISTORY.route -> R.string.nav_history
                    Navigation.INTERVALS.route -> R.string.nav_intervals
                    Navigation.COSTS.route -> R.string.nav_costs
                    else -> R.string.app_name
                }
                TopAppBar(
                    title = { Text(stringResource(titleRes)) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                )
            }
        },
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
                            icon = {
                                Icon(
                                    dest.icon,
                                    contentDescription = stringResource(dest.labelRes)
                                )
                            },
                            label = { Text(stringResource(dest.labelRes)) }
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
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = stringResource(R.string.add)
                            )
                        }
                    }
                }

                Navigation.INTERVALS.route -> {
                    if (vehicle != null) {
                        FloatingActionButton(onClick = { navController.navigate("intervalForm") }) {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = stringResource(R.string.add)
                            )
                        }
                    }
                }

                Navigation.COSTS.route -> {
                    if (vehicle != null) {
                        FloatingActionButton(onClick = { navController.navigate("refuelForm") }) {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = stringResource(R.string.add)
                            )
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
                    onAddClick = { navController.navigate("vehicleForm") },
                    onEditClick = { navController.navigate("vehicleForm") },
                    onUpdateMileage = { showMileageDialog = true }

                )
            }

            composable("vehicleForm") {
                FormScreen(onDone = { navController.popBackStack() })
            }

            composable(Navigation.HISTORY.route) {
                HistoryScreen(
                    onEntryClick = { navController.navigate("maintenanceForm?entryId=${it.id}") }
                )
            }

            composable(
                route = "maintenanceForm?entryId={entryId}",
                arguments = listOf(navArgument("entryId") {
                    type = NavType.LongType
                    defaultValue = 0L
                })
            ) {
                MaintenanceFormScreen(
                    onDone = { navController.popBackStack() }
                )
            }

            composable(Navigation.INTERVALS.route) {
                IntervalsScreen(
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
                    onSave = { intervalViewModel.save(it); navController.popBackStack() },
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
                    lastMileage = homeState.currentKm ?: 0,
                    tankCapacity = vehicle?.tankCapacity,
                    onSave = { refuelViewModel.save(it); navController.popBackStack() },
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
                    lastMileage = homeState.currentKm ?: 0,
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
                CostsScreen(
                    onRefuelClick = { navController.navigate("refuelForm/${it.id}") }
                )
            }
        }
    }

    if (showMileageDialog) {
        UpdateMileageDialog(
            currentKm = homeState.currentKm ?: 0,
            onConfirm = { km ->
                homeVm.addReading(km)
                showMileageDialog = false
            },
            onDismiss = { showMileageDialog = false }
        )
    }
}