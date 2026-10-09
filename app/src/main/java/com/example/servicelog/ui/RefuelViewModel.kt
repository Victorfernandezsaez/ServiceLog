package com.example.servicelog.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.servicelog.data.RefuelRepository
import com.example.servicelog.domain.Refuel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class RefuelViewModel @Inject constructor(
    private val repo: RefuelRepository
) : ViewModel() {

    private val vehicleId = MutableStateFlow<Long?>(null)

    val refuels: StateFlow<UiState<List<Refuel>>> = vehicleId
        .filterNotNull()
        .flatMapLatest { repo.observeRefuels(it) }
        .map<List<Refuel>, UiState<List<Refuel>>> { UiState.Content(it) }
        .catch { emit(UiState.Error(it.message ?: "Unknown error")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    fun setVehicle(id: Long) { vehicleId.value = id }

    fun save(refuel: Refuel) { viewModelScope.launch { repo.save(refuel) } }

    fun delete(refuel: Refuel) { viewModelScope.launch { repo.delete(refuel) } }
}