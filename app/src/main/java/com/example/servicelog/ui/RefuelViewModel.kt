package com.example.servicelog.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servicelog.data.MaintenanceRepository
import com.example.servicelog.domain.averageConsumption
import com.example.servicelog.domain.lastStretchConsumption
import com.example.servicelog.data.RefuelRepository
import com.example.servicelog.domain.CostSummary
import com.example.servicelog.domain.Refuel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class RefuelViewModel @Inject constructor(
    private val repo: RefuelRepository,
    private val maintenanceRepo: MaintenanceRepository
) : ViewModel() {

    private val vehicleId = MutableStateFlow<Long?>(null)

    val refuels: StateFlow<UiState<List<Refuel>>> = vehicleId
        .filterNotNull()
        .flatMapLatest { repo.observeRefuels(it) }
        .map<List<Refuel>, UiState<List<Refuel>>> { UiState.Content(it) }
        .catch { emit(UiState.Error(it.message ?: "Unknown error")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    val summary: StateFlow<UiState<CostSummary>> = vehicleId
        .filterNotNull()
        .flatMapLatest { id ->
            combine(
                repo.observeRefuels(id),
                repo.observeMonthlyFuel(id),
                maintenanceRepo.observeMonthlyMaintenance(id)
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
        .catch { emit(UiState.Error(it.message ?: "Unknown error")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    fun setVehicle(id: Long) { vehicleId.value = id }

    fun save(refuel: Refuel) { viewModelScope.launch { repo.save(refuel) } }

    fun delete(refuel: Refuel) { viewModelScope.launch { repo.delete(refuel) } }
}