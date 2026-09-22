package com.example.servicelog.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class Navigation(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    HOME("home", "Home", Icons.Filled.Home),
    HISTORY("history", "History", Icons.Filled.List),
    INTERVALS("intervals", "Intervals", Icons.Filled.Schedule),
    COSTS("costs", "Costs", Icons.Filled.Euro)
}