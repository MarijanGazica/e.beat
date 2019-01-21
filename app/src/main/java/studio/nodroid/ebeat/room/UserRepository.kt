package studio.nodroid.ebeat.room

import androidx.lifecycle.LiveData
import kotlinx.coroutines.*
import studio.nodroid.ebeat.model.User

class UserRepositoryImpl(private val userDao: UserDao) : UserRepository {

    init {
        GlobalScope.launch(Dispatchers.Default) {
            if (userDao.getAllUsersNow().isEmpty()) {
                addDefaultUser()
            }
        }
    }

    private fun addDefaultUser() {
        GlobalScope.launch(Dispatchers.Default) {
            userDao.insert(User(name = "Default"))
        }
    }

    override fun getAllUsers(): LiveData<List<User>> {
        return userDao.getAllUsers()
    }

    override suspend fun deleteUser(user: User) = coroutineScope {
        async(Dispatchers.Default) {
            userDao.deleteUser(user)
        }.await()
    }

    override suspend fun addUser(user: User) = coroutineScope {
        launch(Dispatchers.Default) {
            userDao.insert(user)
        }
    }

    override suspend fun updateUser(user: User) = coroutineScope {
        user.run {
            async(Dispatchers.Default) {
                userDao.updateUser(this@run)
            }.await()
        }
    }

}

interface UserRepository {
    suspend fun deleteUser(user: User)
    fun getAllUsers(): LiveData<List<User>>
    suspend fun addUser(user: User): Job
    suspend fun updateUser(user: User)
}