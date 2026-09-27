package com.example.servicelog.data

import com.example.servicelog.domain.Category
import com.example.servicelog.domain.VehicleType
import java.time.LocalDate

suspend fun seed(db: AppDatabase) {
    android.util.Log.d("Seed", "start")

    val vehicleId = db.vehicleDao().insertReturningId(
        VehicleEntity(
            type = VehicleType.VAN,
            alias = "La Gordi",
            brand = "VW",
            model = "T4",
            year = 1996,
            typeOfFuel = "Diesel",
            tankCapacity = 80,
            currentKm = 375529
        )
    )

    fun entry(
        title: String,
        category: Category,
        date: LocalDate? = null,
        approx: Boolean = false,
        mileage: Int? = null,
        workshop: String? = null,
        notes: String? = null
    ) = MaintenanceEntryEntity(
        vehicleId = vehicleId,
        title = title,
        category = category,
        date = date,
        dateIsApproximate = approx,
        mileage = mileage,
        costCents = null,
        workshop = workshop,
        notes = notes,
        photoUri = null
    )

    val entries = listOf(
        entry(
            title = "Timing belt change",
            category = Category.ENGINE,
            mileage = 357000
        ),
        entry(
            title = "Alternator brushes",
            category = Category.ELECTRICAL,
            date = LocalDate.of(2022, 6, 1),
            approx = true
        ),
        entry(
            title = "Water pump",
            category = Category.ENGINE,
            date = LocalDate.of(2023, 10, 1),
            approx = true
        ),
        entry(
            title = "Oil + filter change (5w30)",
            category = Category.ENGINE,
            date = LocalDate.of(2024, 5, 1),
            approx = true
        ),
        entry(
            title = "Gear linkage balls and bearings",
            category = Category.TRANSMISSION,
            date = LocalDate.of(2024, 9, 1),
            approx = true
        ),
        entry(
            title = "Front tires, brake pads, rear discs",
            category = Category.BRAKES,
            date = LocalDate.of(2025, 3, 1),
            approx = true,
            mileage = 369000,
            notes = "Brake pads except front right"
        ),
        entry(
            title = "Pollen and air filter",
            category = Category.ENGINE,
            date = LocalDate.of(2025, 7, 1),
            approx = true
        ),
        entry(
            title = "Oil + filter + filter housing gasket (10w40)",
            category = Category.ENGINE,
            date = LocalDate.of(2025, 8, 1),
            approx = true,
            mileage = 371806
        ),
        entry(
            title = "Oil pump, sump gasket, oil + filter (10w40)",
            category = Category.ENGINE,
            date = LocalDate.of(2026, 3, 10),
            mileage = 375529
        ),
        entry(
            title = "Sill welding",
            category = Category.BODY,
            date = LocalDate.of(2026, 5, 1),
            approx = true,
            workshop = "Rob´s Kfz"
        )
    )

    entries.forEach { db.maintenanceDao().save(it) }

    android.util.Log.d("Seed", "done: ${entries.size} entries")
}