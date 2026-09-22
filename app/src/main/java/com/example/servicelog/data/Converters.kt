package com.example.servicelog.data

import androidx.room.TypeConverter
import com.example.servicelog.domain.VehicleType
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromVehicleType(value: VehicleType): String = value.name

    @TypeConverter
    fun toVehicleType(value: String): VehicleType = VehicleType.valueOf(value)
    @TypeConverter
    fun fromDate(value: LocalDate?): Long? = value?.toEpochDay()

    @TypeConverter
    fun toDate(value: Long?): LocalDate? = value?.let { LocalDate.ofEpochDay(it) }
}