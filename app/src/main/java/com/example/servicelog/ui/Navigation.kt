package com.example.servicelog.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.servicelog.R


enum class Navigation(
    val route: String,
    val labelRes: Int,
    val icon: ImageVector
) {
    HOME("home", R.string.nav_home, Icons.Filled.Home),
    HISTORY("history", R.string.nav_history, Icons.AutoMirrored.Filled.List),
    INTERVALS("intervals", R.string.nav_intervals, Icons.Filled.Schedule),
    COSTS("costs", R.string.nav_costs, Icons.Filled.Euro)
}