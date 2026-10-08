package com.example.servicelog.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servicelog.data.VehicleRepository
import com.example.servicelog.domain.Vehicle
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VehicleViewModel(private val repo: VehicleRepository) : ViewModel() {


    val vehicle: StateFlow<UiState<Vehicle?>> = repo.observeVehicle()
        .map<Vehicle?, UiState<Vehicle?>> { UiState.Content(it) }
        .catch { emit(UiState.Error(it.message ?: "Unknown error")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    fun save(vehicle: Vehicle) {
        viewModelScope.launch { repo.save(vehicle) }
    }
}