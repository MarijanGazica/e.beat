package studio.nodroid.ebeat.room

import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.*
import studio.nodroid.ebeat.model.PressureDataDB

class PressureDataRepositoryImpl(private val pressureDataDao: PressureDataDao) : PressureDataRepository {

    override suspend fun addReading(reading: PressureDataDB) = coroutineScope {
        launch(Dispatchers.IO) {
            Log.d("findme", "Saving $reading")
            pressureDataDao.insertPressureData(reading)
        }
    }

    override fun getAllReadings(): LiveData<List<PressureDataDB>> = pressureDataDao.getAllPressureDataLive()

    override suspend fun getAllReadingsFor(userId: Int): List<PressureDataDB> {
        return withContext(Dispatchers.IO) {
            pressureDataDao.getUserPressureData(userId)
        }
    }

    override suspend fun getReadingById(id: Int): PressureDataDB = coroutineScope {
        return@coroutineScope withContext(Dispatchers.IO) { pressureDataDao.getById(id) }
    }

    override suspend fun deleteReading(value: PressureDataDB): Job = coroutineScope {
        launch(Dispatchers.IO) {
            pressureDataDao.deletePressureData(value)
        }
    }
}

interface PressureDataRepository {
    suspend fun addReading(reading: PressureDataDB): Job
    fun getAllReadings(): LiveData<List<PressureDataDB>>
    suspend fun getAllReadingsFor(userId: Int): List<PressureDataDB>
    suspend fun getReadingById(id: Int): PressureDataDB
    suspend fun deleteReading(value: PressureDataDB): Job
}
