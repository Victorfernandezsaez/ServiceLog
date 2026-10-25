package com.example.servicelog.ui.viewModels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servicelog.data.repositories.MaintenanceRepository
import com.example.servicelog.data.repositories.MileageRepository
import com.example.servicelog.data.repositories.VehicleRepository
import com.example.servicelog.domain.MaintenanceEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class MaintenanceFormUiState(
    val entry: MaintenanceEntry? = null,
    val vehicleId: Long? = null,
    val currentKm: Int? = null,
    val lastReadingDate: LocalDate? = null,
    val isLoading: Boolean = true

)

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class MaintenanceFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val maintenanceRepository: MaintenanceRepository,
    vehicleRepository: VehicleRepository,
    mileageRepository: MileageRepository
) : ViewModel() {

    private val entryId: Long = savedStateHandle.get<Long>("entryId") ?: 0L


    val uiState: StateFlow<MaintenanceFormUiState> = vehicleRepository.observeVehicle()
        .flatMapLatest { vehicle ->
            if (vehicle == null) {
                flowOf(MaintenanceFormUiState(isLoading = false))
            } else {
                combine(
                    if (entryId == 0L) flowOf(null) else maintenanceRepository.observeEntry(entryId),
                    mileageRepository.observeLatestReading(vehicle.id)
                ) { entry, reading ->
                    MaintenanceFormUiState(
                        entry = entry,
                        vehicleId = vehicle.id,
                        currentKm = reading?.mileage,
                        lastReadingDate = reading?.date?.let { LocalDate.ofEpochDay(it) },
                        isLoading = false
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MaintenanceFormUiState())

    fun save(entry: MaintenanceEntry) {
        viewModelScope.launch {
            maintenanceRepository.save(entry)
        }
    }

    fun delete(entry: MaintenanceEntry) {
        viewModelScope.launch {
            maintenanceRepository.delete(entry)
        }
    }
}
