package studio.nodroid.ebeat.room

import androidx.lifecycle.LiveData
import androidx.room.*
import studio.nodroid.ebeat.model.PressureDataDB

@Dao
interface PressureDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPressureData(data: PressureDataDB)

    @Query("SELECT * FROM PressureDataDB ORDER BY timestamp")
    fun getAllPressureDataLive(): LiveData<List<PressureDataDB>>

    @Query("SELECT * FROM PressureDataDB")
    fun getAllPressureData(): List<PressureDataDB>

    @Query("SELECT * FROM PressureDataDB WHERE userId = :userId")
    fun getUserPressureData(userId: Int): List<PressureDataDB>

    @Delete
    fun deletePressureData(data: PressureDataDB)

    @Update
    fun updatePressureData(data: PressureDataDB)

    @Query("SELECT * FROM PressureDataDB WHERE timestamp > :start AND timestamp < :end")
    fun getDataForRange(start: Long, end: Long): List<PressureDataDB>

    @Query("SELECT * FROM PressureDataDB WHERE userId = :userId AND timestamp > :start AND timestamp < :end")
    fun getDataForUserAndRange(userId: Int, start: Long, end: Long): LiveData<List<PressureDataDB>>

    @Query("SELECT * FROM PressureDataDB WHERE id = :id")
    fun getById(id: Int): PressureDataDB
}