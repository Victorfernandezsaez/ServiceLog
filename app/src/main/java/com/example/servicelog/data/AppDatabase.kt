package com.example.servicelog.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        VehicleEntity::class,
        MaintenanceEntryEntity::class,
        RefuelEntity::class,
        MaintenanceIntervalEntity::class
    ],
    version = 6,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun vehicleDao(): VehicleDao
    abstract fun maintenanceDao(): MaintenanceDao

    abstract fun refuelDao(): RefuelDao

    abstract fun maintenanceIntervalDao(): MaintenanceIntervalDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "servicelog.db"
                )
                    .fallbackToDestructiveMigration(true)
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            android.util.Log.d("Seed", "onCreate fired")
                            CoroutineScope(Dispatchers.IO).launch {
                                seed(get(context))
                            }
                        }
                    })
                    .build()
                    .also { INSTANCE = it }
            }
    }
}