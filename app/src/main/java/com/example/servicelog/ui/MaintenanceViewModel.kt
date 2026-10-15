package com.example.servicelog.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servicelog.data.MaintenanceRepository
import com.example.servicelog.data.VehicleRepository
import com.example.servicelog.domain.MaintenanceEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MaintenanceViewModel @Inject constructor(
    private val repo: MaintenanceRepository,
    private val vehicleRepo: VehicleRepository
) : ViewModel() {
    private val vehicleId = MutableStateFlow<Long?>(null)

    val entries: StateFlow<UiState<List<MaintenanceEntry>>> = vehicleId
        .filterNotNull()
        .flatMapLatest { repo.observeEntries(it) }
        .map<List<MaintenanceEntry>, UiState<List<MaintenanceEntry>>> { UiState.Content(it) }
        .catch { emit(UiState.Error(it.message ?: "Unknown error")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    fun setVehicle(id: Long) { vehicleId.value = id }

    fun save(entry: MaintenanceEntry) {
        viewModelScope.launch {
            repo.save(entry)
            entry.mileage?.let {
                vehicleRepo.updateMileageIfHigher(it, entry.date ?: LocalDate.now())
            }
        }
    }

    fun delete(entry: MaintenanceEntry) {
        viewModelScope.launch { repo.delete(entry) }
    }
}