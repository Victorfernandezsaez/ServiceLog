package com.example.servicelog.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servicelog.data.MaintenanceRepository
import com.example.servicelog.data.RefuelRepository
import com.example.servicelog.data.VehicleRepository
import com.example.servicelog.domain.CostSummary
import com.example.servicelog.domain.Refuel
import com.example.servicelog.domain.averageConsumption
import com.example.servicelog.domain.lastStretchConsumption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class RefuelViewModel @Inject constructor(
    private val repo: RefuelRepository,
    private val maintenanceRepo: MaintenanceRepository,
    private val vehicleRepo: VehicleRepository
) : ViewModel() {

    val refuels: StateFlow<UiState<List<Refuel>>> = vehicleRepo.observeVehicle()
        .filterNotNull()
        .flatMapLatest { repo.observeRefuels(it.id) }
        .map<List<Refuel>, UiState<List<Refuel>>> { UiState.Content(it) }
        .catch { emit(UiState.Error("Unknown error")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    val summary: StateFlow<UiState<CostSummary>> = vehicleRepo.observeVehicle()
        .filterNotNull()
        .flatMapLatest { vehicle ->
            combine(
                repo.observeRefuels(vehicle.id),
                repo.observeMonthlyFuel(vehicle.id),
                maintenanceRepo.observeMonthlyMaintenance(vehicle.id)
            ) { refuels, fuel, maintenance ->
                CostSummary(
                    refuels = refuels,
                    monthlyFuelCents = fuel,
                    monthlyMaintenanceCents = maintenance,
                    average = averageConsumption(refuels),
                    lastStretch = lastStretchConsumption(refuels)
                )
            }
        }
        .map<CostSummary, UiState<CostSummary>> { UiState.Content(it) }
        .catch { emit(UiState.Error("Unknown error")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    fun save(refuel: Refuel) {
        viewModelScope.launch {
            repo.save(refuel)
            vehicleRepo.updateMileageIfHigher(refuel.mileage, refuel.date)
        }
    }

    fun delete(refuel: Refuel) { viewModelScope.launch { repo.delete(refuel) } }
}