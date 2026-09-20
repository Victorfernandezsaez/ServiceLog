package com.example.servicelog.data

import androidx.room.TypeConverter
import com.example.servicelog.domain.VehicleType

class Converters {
    @TypeConverter
    fun fromVehicleType(value: VehicleType): String = value.name

    @TypeConverter
    fun toVehicleType(value: String): VehicleType = VehicleType.valueOf(value)
}