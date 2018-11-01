package studio.nodroid.bloodpressurehelper.room

import androidx.lifecycle.LiveData
import androidx.room.*
import studio.nodroid.bloodpressurehelper.model.PressureDataDB

@Dao
interface PressureDataDao {

    @Insert
    fun insertPressureData(data: PressureDataDB)

    @Query("SELECT * FROM PressureDataDB")
    fun getAllPressureData(): LiveData<List<PressureDataDB>>

    @Query("SELECT * FROM PressureDataDB WHERE id = :userId")
    fun getUserPressureData(userId: Int): LiveData<List<PressureDataDB>>

    @Delete
    fun deletePressureData(data: PressureDataDB)

    @Update
    fun updatePressureData(data: PressureDataDB)
}