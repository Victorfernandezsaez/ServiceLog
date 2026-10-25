package com.example.servicelog.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servicelog.data.repositories.MaintenanceRepository
import com.example.servicelog.data.repositories.RefuelRepository
import com.example.servicelog.data.repositories.VehicleRepository
import com.example.servicelog.domain.CostSummary
import com.example.servicelog.domain.Refuel
import com.example.servicelog.domain.averageConsumption
import com.example.servicelog.domain.lastStretchConsumption
import com.example.servicelog.ui.uistate.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class RefuelViewModel @Inject constructor(
    private val repo: RefuelRepository,
    private val maintenanceRepo: MaintenanceRepository,
    vehicleRepo: VehicleRepository
) : ViewModel() {

    val refuels: StateFlow<UiState<List<Refuel>>> = vehicleRepo.observeVehicle()
        .flatMapLatest { vehicle ->
            if (vehicle == null) flowOf(emptyList())
            else repo.observeRefuels(vehicle.id)
        }
        .map<List<Refuel>, UiState<List<Refuel>>> { UiState.Content(it) }
        .catch { emit(UiState.Error(it.message ?: "Unknown error")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    val summary: StateFlow<UiState<CostSummary>> = vehicleRepo.observeVehicle()
        .flatMapLatest { vehicle ->
            if (vehicle == null) {
                flowOf(CostSummary(emptyList(), emptyMap(), emptyMap(), null, null))
            } else {
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
        }
        .map<CostSummary, UiState<CostSummary>> { UiState.Content(it) }
        .catch { emit(UiState.Error(it.message ?: "Unknown error")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    fun save(entry: Refuel) {
        viewModelScope.launch { repo.save(entry) }
    }

    fun delete(refuel: Refuel) { viewModelScope.launch { repo.delete(refuel) } }
}