package com.example.servicelog.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servicelog.data.repositories.VehicleRepository
import com.example.servicelog.domain.Vehicle
import com.example.servicelog.ui.uistate.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class VehicleViewModel @Inject constructor(private val repo: VehicleRepository) : ViewModel() {


    val vehicle: StateFlow<UiState<Vehicle?>> = repo.observeVehicle()
        .map<Vehicle?, UiState<Vehicle?>> { UiState.Content(it) }
        .catch { emit(UiState.Error(it.message ?: "Unknown error")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    fun save(vehicle: Vehicle) {
        viewModelScope.launch { repo.save(vehicle) }
    }
}