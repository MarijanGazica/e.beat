package studio.nodroid.ebeat.room

import androidx.lifecycle.LiveData
import kotlinx.coroutines.*
import studio.nodroid.ebeat.model.User

class UserRepositoryImpl(private val userDao: UserDao) : UserRepository {

    override fun getAllUsers(): LiveData<List<User>> {
        return userDao.getAllUsers()
    }

    override suspend fun getAllUsersList(): List<User> {
        return withContext(Dispatchers.IO) {
            userDao.getAllUsersNow()
        }
    }

    override suspend fun deleteUser(user: User) = coroutineScope {
        withContext(Dispatchers.IO) {
            userDao.deleteUser(user)
        }
    }

    override suspend fun addUser(user: User) = coroutineScope {
        launch(Dispatchers.IO) {
            userDao.insert(user)
        }
    }

    override suspend fun updateUser(user: User) = coroutineScope {
        withContext(Dispatchers.IO) {
            userDao.updateUser(user)
        }
    }

}

interface UserRepository {
    suspend fun deleteUser(user: User)
    fun getAllUsers(): LiveData<List<User>>
    suspend fun getAllUsersList(): List<User>
    suspend fun addUser(user: User): Job
    suspend fun updateUser(user: User)
}