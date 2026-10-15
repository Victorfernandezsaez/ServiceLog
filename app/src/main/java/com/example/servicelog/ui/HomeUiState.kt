package com.example.servicelog.ui

import com.example.servicelog.domain.DueStatus
import com.example.servicelog.domain.Vehicle

data class HomeUiState(
    val vehicle: Vehicle? = null,
    val dueStatuses: List<DueStatus> = emptyList(),
    val isLoading: Boolean = true
)