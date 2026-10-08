package com.example.servicelog

import android.app.Application
import com.example.servicelog.data.*

class ServiceLogApp : Application() {
    val db by lazy { AppDatabase.get(this) }
    val vehicleRepo by lazy { VehicleRepository(db.vehicleDao()) }
    val maintenanceRepo by lazy { MaintenanceRepository(db.maintenanceDao()) }
    val intervalRepo by lazy { IntervalRepository(db.maintenanceIntervalDao()) }
}