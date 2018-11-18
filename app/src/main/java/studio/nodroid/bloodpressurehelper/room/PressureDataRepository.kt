package studio.nodroid.bloodpressurehelper.room

import androidx.lifecycle.LiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import studio.nodroid.bloodpressurehelper.model.PressureDataDB
import java.util.*

class PressureDataRepositoryImpl(private val pressureDataDao: PressureDataDao) : PressureDataRepository {

    private val day = 1000 * 60 * 60 * 24

    override fun addReading(reading: PressureDataDB) {
        GlobalScope.launch { pressureDataDao.insertPressureData(reading) }
    }

    override fun getAllReadings(): LiveData<List<PressureDataDB>> = pressureDataDao.getAllPressureDataLive()

    override suspend fun getReadingsForDate(date: Date): List<PressureDataDB> {
        return GlobalScope.async {
            val cal = Calendar.getInstance()
            cal.time = date
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)

            val start = cal.timeInMillis
            val end = start + day - 1

            pressureDataDao.getAllPressureData()
                .filter { it.timestamp < end }
                .filter { it.timestamp > start }
        }.await()
    }

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

    suspend fun getReadingsForDate(date: Date): List<PressureDataDB>
}