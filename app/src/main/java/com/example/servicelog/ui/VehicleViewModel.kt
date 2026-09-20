package com.example.servicelog.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.servicelog.data.AppDatabase
import com.example.servicelog.data.VehicleRepository
import com.example.servicelog.domain.Vehicle
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VehicleViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = VehicleRepository(AppDatabase.get(app).vehicleDao())

    val vehicle: StateFlow<Vehicle?> = repo.observeVehicle()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun save(vehicle: Vehicle) {
        viewModelScope.launch { repo.save(vehicle) }
    }
}