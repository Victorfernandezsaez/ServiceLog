package com.example.servicelog.ui.uistate

import com.example.servicelog.domain.DueStatus
import com.example.servicelog.domain.Vehicle
import java.time.LocalDate

data class HomeUiState(
    val vehicle: Vehicle? = null,
    val dueStatuses: List<DueStatus> = emptyList(),
    val isLoading: Boolean = true,
    val currentKm: Int? = null,
    val lastReadingDate: LocalDate? = null
)
