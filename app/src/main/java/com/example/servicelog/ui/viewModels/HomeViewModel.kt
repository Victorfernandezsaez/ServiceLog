package com.example.servicelog.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servicelog.data.repositories.IntervalRepository
import com.example.servicelog.data.repositories.MaintenanceRepository
import com.example.servicelog.data.repositories.MileageRepository
import com.example.servicelog.data.repositories.VehicleRepository
import com.example.servicelog.domain.calculateDue
import com.example.servicelog.ui.uistate.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    vehicleRepo: VehicleRepository,
    private val intervalRepo: IntervalRepository,
    private val maintenanceRepo: MaintenanceRepository,
    private val mileageRepo: MileageRepository
) : ViewModel() {

    fun addReading(km: Int) {
        viewModelScope.launch {
            val id = uiState.value.vehicle?.id ?: return@launch
            mileageRepo.addReading(id, km, LocalDate.now())
        }
    }
    val uiState: StateFlow<HomeUiState> = vehicleRepo.observeVehicle()
        .flatMapLatest { vehicle ->
            if (vehicle == null) {
                flowOf(HomeUiState(vehicle = null, isLoading = false))
            } else {
                combine(
                    intervalRepo.observeIntervals(vehicle.id),
                    maintenanceRepo.observeEntries(vehicle.id),
                    mileageRepo.observeLatestReading(vehicle.id)
                ) { intervals, entries, reading ->
                    HomeUiState(
                        vehicle = vehicle,
                        currentKm = reading?.mileage,
                        lastReadingDate = reading?.date?.let { LocalDate.ofEpochDay(it) },
                        dueStatuses = intervals
                            .map { calculateDue(it, entries, LocalDate.now(), reading?.mileage) }
                            .sortedBy { it.urgency.ordinal },
                        isLoading = false
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())
}