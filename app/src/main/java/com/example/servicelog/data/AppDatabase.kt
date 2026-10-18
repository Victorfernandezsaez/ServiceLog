package com.example.servicelog.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.servicelog.data.daos.MaintenanceDao
import com.example.servicelog.data.daos.MaintenanceIntervalDao
import com.example.servicelog.data.daos.MileageDao
import com.example.servicelog.data.daos.RefuelDao
import com.example.servicelog.data.daos.VehicleDao
import com.example.servicelog.data.entities.MaintenanceEntryEntity
import com.example.servicelog.data.entities.MaintenanceIntervalEntity
import com.example.servicelog.data.entities.MileageReadingEntity
import com.example.servicelog.data.entities.RefuelEntity
import com.example.servicelog.data.entities.VehicleEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        VehicleEntity::class,
        MaintenanceEntryEntity::class,
        RefuelEntity::class,
        MaintenanceIntervalEntity::class,
        MileageReadingEntity:: class
    ],
    version = 8,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun vehicleDao(): VehicleDao
    abstract fun maintenanceDao(): MaintenanceDao

    abstract fun refuelDao(): RefuelDao

    abstract fun maintenanceIntervalDao(): MaintenanceIntervalDao

    abstract fun mileageDao(): MileageDao

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