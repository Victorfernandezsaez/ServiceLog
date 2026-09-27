package com.example.servicelog.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.servicelog.data.AppDatabase
import com.example.servicelog.data.MaintenanceRepository
import com.example.servicelog.domain.MaintenanceEntry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class MaintenanceViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = MaintenanceRepository(AppDatabase.get(app).maintenanceDao())

    private val vehicleId = MutableStateFlow<Long?>(null)

    val entries: StateFlow<List<MaintenanceEntry>> = vehicleId
        .filterNotNull()
        .flatMapLatest { repo.observeEntries(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setVehicle(id: Long) { vehicleId.value = id }

    fun save(entry: MaintenanceEntry) {
        viewModelScope.launch { repo.save(entry) }
    }

    fun delete(entry: MaintenanceEntry) {
        viewModelScope.launch { repo.delete(entry) }
    }
}