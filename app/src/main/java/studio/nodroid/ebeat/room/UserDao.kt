package studio.nodroid.ebeat.room

import androidx.lifecycle.LiveData
import androidx.room.*
import studio.nodroid.ebeat.model.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User)

    @Query("SELECT * FROM User")
    fun getAllUsers(): LiveData<List<User>>

    @Query("SELECT * FROM User")
    fun getAllUsersNow(): List<User>

    @Delete
    fun deleteUser(user: User)

    @Update
    fun updateUser(user: User)

    @Query("SELECT * FROM User WHERE id = :userId LIMIT 1")
    fun getUserById(userId: Int): User
}