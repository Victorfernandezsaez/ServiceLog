package com.example.servicelog.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import com.example.servicelog.ServiceLogApp

val AppViewModelFactory = viewModelFactory {
    initializer { VehicleViewModel(app().vehicleRepo) }
    initializer { MaintenanceViewModel(app().maintenanceRepo) }
    initializer { IntervalViewModel(app().intervalRepo) }
}

private fun CreationExtras.app() = this[APPLICATION_KEY] as ServiceLogApp