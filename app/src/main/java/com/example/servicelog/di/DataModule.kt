package com.example.servicelog.di

import android.content.Context
import com.example.servicelog.data.AppDatabase
import com.example.servicelog.data.IntervalRepository
import com.example.servicelog.data.MaintenanceRepository
import com.example.servicelog.data.RefuelRepository
import com.example.servicelog.data.VehicleRepository
import javax.inject.Singleton
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        AppDatabase.get(context)

    @Provides fun provideVehicleRepo(db: AppDatabase) = VehicleRepository(db.vehicleDao())
    @Provides fun provideMaintenanceRepo(db: AppDatabase) =
        MaintenanceRepository(db.maintenanceDao())
    @Provides fun provideIntervalRepo(db: AppDatabase) =
        IntervalRepository(db.maintenanceIntervalDao())

    @Provides fun provideRefuelRepo(db: AppDatabase) = RefuelRepository(db.refuelDao())
}