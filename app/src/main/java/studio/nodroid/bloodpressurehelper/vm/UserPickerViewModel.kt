package studio.nodroid.bloodpressurehelper.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import studio.nodroid.bloodpressurehelper.model.User
import studio.nodroid.bloodpressurehelper.room.UserRepository
import studio.nodroid.bloodpressurehelper.sharedPrefs.SharedPrefs

class UserPickerViewModel(
    userRepository: UserRepository,
    private val sharedPrefs: SharedPrefs
) : ViewModel() {

    val allUsers = userRepository.getAllUsers()

    val activeUser = MutableLiveData<User>()

    val onUserSelected: (User) -> Unit = {
        activeUser.value = allUsers.value?.firstOrNull { item -> item.id == sharedPrefs.getLastUserId() }
    }

    fun usersAvailable() {
        activeUser.value = allUsers.value?.firstOrNull {
            it.id == sharedPrefs.getLastUserId()
        }

        if (activeUser.value == null) {
            val user = allUsers.value?.firstOrNull()
            user?.run {
                sharedPrefs.saveLastUserId(id)
                onUserSelected(this)
            }
        }
    }

}
