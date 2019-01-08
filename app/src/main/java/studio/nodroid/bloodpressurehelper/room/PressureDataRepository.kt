package studio.nodroid.bloodpressurehelper.room

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import studio.nodroid.bloodpressurehelper.model.PressureDataDB

class PressureDataRepositoryImpl(private val pressureDataDao: PressureDataDao) : PressureDataRepository {

    override suspend fun addReading(reading: PressureDataDB) = coroutineScope {
        launch { pressureDataDao.insertPressureData(reading) }
    }

    override fun getAllReadings(): LiveData<List<PressureDataDB>> = pressureDataDao.getAllPressureDataLive()
}

interface PressureDataRepository {
    suspend fun addReading(reading: PressureDataDB): Job

    fun getAllReadings(): LiveData<List<PressureDataDB>>
}
