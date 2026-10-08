package com.example.servicelog.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servicelog.data.AppDatabase
import com.example.servicelog.data.MaintenanceRepository
import com.example.servicelog.domain.MaintenanceEntry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class MaintenanceViewModel(private val repo: MaintenanceRepository) : ViewModel() {
    private val vehicleId = MutableStateFlow<Long?>(null)

    val entries: StateFlow<UiState<List<MaintenanceEntry>>> = vehicleId
        .filterNotNull()
        .flatMapLatest { repo.observeEntries(it) }
        .map<List<MaintenanceEntry>, UiState<List<MaintenanceEntry>>> { UiState.Content(it) }
        .catch { emit(UiState.Error(it.message ?: "Unknown error")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    fun setVehicle(id: Long) { vehicleId.value = id }

    fun save(entry: MaintenanceEntry) {
        viewModelScope.launch { repo.save(entry) }
    }

    fun delete(entry: MaintenanceEntry) {
        viewModelScope.launch { repo.delete(entry) }
    }
}