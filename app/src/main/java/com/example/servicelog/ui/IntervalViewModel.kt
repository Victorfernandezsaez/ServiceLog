package com.example.servicelog.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.servicelog.data.AppDatabase
import com.example.servicelog.data.IntervalRepository
import com.example.servicelog.domain.MaintenanceInterval
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class IntervalViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = IntervalRepository(AppDatabase.get(app).maintenanceIntervalDao())
    private val vehicleId = MutableStateFlow<Long?>(null)

    val intervals: StateFlow<List<MaintenanceInterval>> = vehicleId
        .filterNotNull()
        .flatMapLatest { repo.observeIntervals(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setVehicle(id: Long) { vehicleId.value = id }
    fun save(interval: MaintenanceInterval) { viewModelScope.launch { repo.save(interval) } }
    fun delete(interval: MaintenanceInterval) { viewModelScope.launch { repo.delete(interval) } }
}