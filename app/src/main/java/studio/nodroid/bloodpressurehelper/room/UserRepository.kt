package studio.nodroid.bloodpressurehelper.room

import androidx.lifecycle.LiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import studio.nodroid.bloodpressurehelper.model.User

class UserRepositoryImpl(private val userDao: UserDao) : UserRepository {

    override fun deleteUser(user: User) {
        GlobalScope.launch {
            userDao.deleteUser(user)
        }
    }

    override fun getAllUsers(): LiveData<List<User>> {
        return userDao.getAllUsers()
    }

    override fun addUser(user: User) {
        GlobalScope.launch {
            userDao.insert(user)
        }
    }

    override fun addDefaultUser() {
        GlobalScope.launch {
            userDao.insert(User(name = "Default"))
        }
    }
}

interface UserRepository {
    fun deleteUser(user: User)
    fun getAllUsers(): LiveData<List<User>>
    fun addUser(user: User)
    fun addDefaultUser()
}