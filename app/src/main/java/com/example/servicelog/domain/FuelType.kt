package com.example.servicelog.domain

import com.example.servicelog.R

enum class FuelType(val labelRes: Int) {
    DIESEL(R.string.fuel_diesel),
    GASOLINE(R.string.fuel_gasoline),
    LPG(R.string.fuel_lpg),
    ELECTRIC(R.string.fuel_electric),
    HYBRID(R.string.fuel_hybrid)
}
