package com.example.servicelog.data.repositories

import com.example.servicelog.data.daos.MileageDao
import com.example.servicelog.data.daos.MileageReading
import com.example.servicelog.data.entities.MileageReadingEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject


class MileageRepository @Inject constructor(private val dao: MileageDao) {

    fun observeLatestReading(vehicleId: Long): Flow<MileageReading?> =
        dao.observeLatestReading(vehicleId)

    suspend fun addReading(vehicleId: Long, mileage: Int, date: LocalDate) =
        dao.insert(MileageReadingEntity(vehicleId = vehicleId, date = date, mileage = mileage))
}