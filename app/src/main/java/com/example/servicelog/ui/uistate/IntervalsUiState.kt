package com.example.servicelog.ui.uistate

import com.example.servicelog.domain.MaintenanceInterval

data class IntervalsUiState(
    val intervals: List<MaintenanceInterval> = emptyList(),
    val isLoading: Boolean = true
)
