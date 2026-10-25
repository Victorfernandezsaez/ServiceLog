package com.example.servicelog.data

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.servicelog.data.repositories.IntervalRepository
import com.example.servicelog.data.repositories.MaintenanceRepository
import com.example.servicelog.data.repositories.MileageRepository
import com.example.servicelog.data.repositories.VehicleRepository
import com.example.servicelog.domain.calculateDue
import com.example.servicelog.domain.remindersFor
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.LocalDate

@HiltWorker
class DueReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val vehicleRepo: VehicleRepository,
    private val intervalRepo: IntervalRepository,
    private val maintenanceRepo: MaintenanceRepository,
    private val mileageRepo: MileageRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val vehicle = vehicleRepo.observeVehicle().first() ?: return Result.success()

        val intervals = intervalRepo.observeIntervals(vehicle.id).first()
        val entries = maintenanceRepo.observeEntries(vehicle.id).first()
        val reading = mileageRepo.observeLatestReading(vehicle.id).first()

        val statuses = intervals.map {
            calculateDue(it, entries, LocalDate.now(), reading?.mileage)
        }

        ensureNotificationChannel(applicationContext)
        remindersFor(statuses).forEach {
            showDueNotification(applicationContext, it)
        }

        return Result.success()
    }
}