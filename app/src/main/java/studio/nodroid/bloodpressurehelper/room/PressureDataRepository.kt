package studio.nodroid.bloodpressurehelper.room

import androidx.lifecycle.LiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import studio.nodroid.bloodpressurehelper.model.PressureDataDB

class PressureDataRepositoryImpl(private val pressureDataDao: PressureDataDao) : PressureDataRepository {

    override fun addReading(reading: PressureDataDB) {
        GlobalScope.launch { pressureDataDao.insertPressureData(reading) }
    }

    override fun getAllReadings(): LiveData<List<PressureDataDB>> = pressureDataDao.getAllPressureData()
}

interface PressureDataRepository {
    fun addReading(reading: PressureDataDB)

    fun getAllReadings(): LiveData<List<PressureDataDB>>
}