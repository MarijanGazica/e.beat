package studio.nodroid.bloodpressurehelper.room

import androidx.lifecycle.LiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import studio.nodroid.bloodpressurehelper.model.PressureDataDB

class PressureDataRepositoryImpl(private val pressureDataDao: PressureDataDao) : PressureDataRepository {

    override fun addReading(reading: PressureDataDB) {
        GlobalScope.launch { pressureDataDao.insertPressureData(reading) }
    }

    override fun getAllReadings(): LiveData<List<PressureDataDB>> = pressureDataDao.getAllPressureData()

    override suspend fun getReadingsForUser(id: Int): List<PressureDataDB> {
        return GlobalScope.async {
            pressureDataDao.getUserPressureData(id)
        }.await()
    }
}

interface PressureDataRepository {
    fun addReading(reading: PressureDataDB)

    fun getAllReadings(): LiveData<List<PressureDataDB>>

    suspend fun getReadingsForUser(id: Int): List<PressureDataDB>
}