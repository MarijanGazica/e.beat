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
        userDao.deleteUser(user)
    }

    override suspend fun addUser(user: User) = coroutineScope {
        launch {
            userDao.insert(user)
        }
    }

    override suspend fun updateUser(copy: User?) = coroutineScope {
        copy?.run {
            userDao.updateUser(this)
        }
    }
}

interface UserRepository {
    suspend fun deleteUser(user: User)
    fun getAllUsers(): LiveData<List<User>>
    suspend fun addUser(user: User): Job
    suspend fun updateUser(copy: User?): Unit?
}