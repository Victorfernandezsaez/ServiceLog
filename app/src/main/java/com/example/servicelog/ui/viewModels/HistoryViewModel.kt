package com.example.servicelog.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servicelog.data.repositories.MaintenanceRepository
import com.example.servicelog.data.repositories.VehicleRepository
import com.example.servicelog.ui.uistate.HistoryUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val vehicleRepo: VehicleRepository,
    private val maintenanceRepo: MaintenanceRepository
) : ViewModel() {

    val uiState: StateFlow<HistoryUiState> = vehicleRepo.observeVehicle()
        .flatMapLatest { vehicle ->
            if (vehicle == null) {
                flowOf(HistoryUiState(entries = emptyList(), isLoading = false))
            } else {
                maintenanceRepo.observeEntries(vehicle.id)
                    .map { entries ->
                        HistoryUiState(
                            entries = entries,
                            isLoading = false
                        )
                    }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HistoryUiState())
}
