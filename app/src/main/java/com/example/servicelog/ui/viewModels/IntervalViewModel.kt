package com.example.servicelog.ui.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servicelog.data.repositories.IntervalRepository
import com.example.servicelog.domain.MaintenanceInterval
import com.example.servicelog.ui.uistate.UiState
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
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class IntervalViewModel @Inject constructor(private val repo: IntervalRepository) : ViewModel() {
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