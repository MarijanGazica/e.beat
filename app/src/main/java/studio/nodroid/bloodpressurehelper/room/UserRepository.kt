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

    override fun getUserById(userId: Int): LiveData<User> {
        return userDao.getUserById(userId)
    }

    override fun updateUser(copy: User?) {
        GlobalScope.launch {
            copy?.run {
                userDao.updateUser(this)
            }
        }
    }
}

interface UserRepository {
    fun deleteUser(user: User)
    fun getAllUsers(): LiveData<List<User>>
    fun addUser(user: User)
    fun addDefaultUser()
    fun getUserById(userId: Int): LiveData<User>
    fun updateUser(copy: User?)
}