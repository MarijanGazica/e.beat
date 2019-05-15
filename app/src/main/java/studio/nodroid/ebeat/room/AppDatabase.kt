package studio.nodroid.ebeat.room

import androidx.room.Database
import androidx.room.RoomDatabase
import studio.nodroid.ebeat.model.PressureDataDB
import studio.nodroid.ebeat.model.User

@Database(entities = [User::class, PressureDataDB::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun pressureDataDao(): PressureDataDao
}