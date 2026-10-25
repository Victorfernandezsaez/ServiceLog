package com.example.servicelog.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servicelog.data.repositories.IntervalRepository
import com.example.servicelog.data.repositories.VehicleRepository
import com.example.servicelog.ui.uistate.IntervalsUiState
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
class IntervalsViewModel @Inject constructor(
    private val vehicleRepo: VehicleRepository,
    private val intervalRepo: IntervalRepository
) : ViewModel() {

    val uiState: StateFlow<IntervalsUiState> = vehicleRepo.observeVehicle()
        .flatMapLatest { vehicle ->
            if (vehicle == null) {
                flowOf(IntervalsUiState(intervals = emptyList(), isLoading = false))
            } else {
                intervalRepo.observeIntervals(vehicle.id)
                    .map { intervals ->
                        IntervalsUiState(
                            intervals = intervals,
                            isLoading = false
                        )
                    }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), IntervalsUiState())
}
