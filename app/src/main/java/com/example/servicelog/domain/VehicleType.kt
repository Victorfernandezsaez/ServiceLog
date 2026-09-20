package com.example.servicelog.domain

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.AirportShuttle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Motorcycle
import androidx.compose.ui.graphics.vector.ImageVector

enum class VehicleType(val label: String) {
    CAR("Car"),
    VAN("Van"),
    TRUCK("Truck"),
    MOTORCYCLE("Motorcycle"),
    BICYCLE("Bicycle")
}

val VehicleType.icon: ImageVector
    get() = when (this) {
        VehicleType.CAR -> Icons.Filled.DirectionsCar
        VehicleType.VAN -> Icons.Filled.AirportShuttle
        VehicleType.TRUCK -> Icons.Filled.LocalShipping
        VehicleType.MOTORCYCLE -> Icons.Filled.Motorcycle
        VehicleType.BICYCLE -> Icons.AutoMirrored.Filled.DirectionsBike
    }