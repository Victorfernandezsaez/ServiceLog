package com.example.servicelog.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servicelog.data.repositories.MileageRepository
import com.example.servicelog.data.repositories.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import com.example.servicelog.domain.Vehicle

    @HiltViewModel
    class VehicleFormViewModel @Inject constructor(
        private val vehicleRepo: VehicleRepository,
        private val mileageRepo: MileageRepository
    ) : ViewModel() {

        val vehicle: StateFlow<Vehicle?> = vehicleRepo.observeVehicle()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

        fun save(vehicle: Vehicle, initialMileage: Int?) {
            viewModelScope.launch {
                val id = vehicleRepo.saveReturningId(vehicle)
                initialMileage?.let { mileageRepo.addReading(id, it, LocalDate.now()) }
            }
        }
    }
