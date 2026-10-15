package com.example.servicelog.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servicelog.data.IntervalRepository
import com.example.servicelog.data.MaintenanceRepository
import com.example.servicelog.data.VehicleRepository
import com.example.servicelog.domain.Vehicle
import com.example.servicelog.domain.calculateDue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val vehicleRepo: VehicleRepository,
    private val intervalRepo: IntervalRepository,
    private val maintenanceRepo: MaintenanceRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = vehicleRepo.observeVehicle()
        .flatMapLatest { vehicle ->
            if (vehicle == null) {
                flowOf(HomeUiState(vehicle = null, isLoading = false))
            } else {
                combine(
                    intervalRepo.observeIntervals(vehicle.id),
                    maintenanceRepo.observeEntries(vehicle.id)
                ) { intervals, entries ->
                    HomeUiState(
                        vehicle = vehicle,
                        dueStatuses = intervals
                            .map { calculateDue(it, entries, LocalDate.now(), vehicle.currentKm) }
                            .sortedBy { it.urgency.ordinal },
                        isLoading = false
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun updateMileage(km: Int) {
        viewModelScope.launch {
            vehicleRepo.updateMileageIfHigher(km, LocalDate.now())
        }
    }
}