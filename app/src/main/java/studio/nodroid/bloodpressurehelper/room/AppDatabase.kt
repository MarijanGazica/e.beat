package studio.nodroid.bloodpressurehelper.room

import androidx.room.Database
import androidx.room.RoomDatabase
import studio.nodroid.bloodpressurehelper.model.PressureDataDB
import studio.nodroid.bloodpressurehelper.model.User

@Database(entities = [User::class, PressureDataDB::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun pressureDataDao(): PressureDataDao
}