package com.example.servicelog.ui.uistate

import com.example.servicelog.domain.MaintenanceEntry

data class HistoryUiState(
    val entries: List<MaintenanceEntry> = emptyList(),
    val isLoading: Boolean = true
)
