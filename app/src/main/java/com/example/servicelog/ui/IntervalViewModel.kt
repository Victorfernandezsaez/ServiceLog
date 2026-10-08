package com.example.servicelog.ui
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servicelog.data.IntervalRepository
import com.example.servicelog.domain.MaintenanceInterval
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class IntervalViewModel(private val repo: IntervalRepository) : ViewModel() {
    private val vehicleId = MutableStateFlow<Long?>(null)

    val intervals: StateFlow<UiState<List<MaintenanceInterval>>> = vehicleId
        .filterNotNull()
        .flatMapLatest { repo.observeIntervals(it) }
        .map<List<MaintenanceInterval>, UiState<List<MaintenanceInterval>>> { UiState.Content(it) }
        .catch { emit(UiState.Error(it.message ?: "Unknown error")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    fun setVehicle(id: Long) { vehicleId.value = id }

    fun save(interval: MaintenanceInterval) {
        viewModelScope.launch { repo.save(interval) }
    }

    fun delete(interval: MaintenanceInterval) {
        viewModelScope.launch { repo.delete(interval) }
    }
}