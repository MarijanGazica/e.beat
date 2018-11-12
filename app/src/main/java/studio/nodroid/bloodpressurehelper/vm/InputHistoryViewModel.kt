package studio.nodroid.bloodpressurehelper.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import studio.nodroid.bloodpressurehelper.model.PressureDataDB
import studio.nodroid.bloodpressurehelper.model.User
import studio.nodroid.bloodpressurehelper.room.PressureDataRepository
import studio.nodroid.bloodpressurehelper.room.UserRepository
import studio.nodroid.bloodpressurehelper.sharedPrefs.SharedPrefs

class InputHistoryViewModel(
    private val pressureDataRepo: PressureDataRepository,
    private val userRepository: UserRepository,
    private val sharedPrefs: SharedPrefs
) : ViewModel() {

    val selectedUser = MutableLiveData<User>()
    val selectedUserReadings = MutableLiveData<List<PressureDataDB>>()
    val allUsers = userRepository.getAllUsers()

    val userSelected: (User) -> Unit = {
        selectedUser.value = it
        GlobalScope.launch(Dispatchers.Main) {
            selectedUserReadings.value = pressureDataRepo.getReadingsForUser(it.id)
        }
    }

    fun findLastUser() {
        val id = sharedPrefs.getLastUserId()
        val lastUser = allUsers.value?.firstOrNull { it.id == id }
        lastUser?.run {
            userSelected(this)
        } ?: setFirstUserActive()
    }

    private fun setFirstUserActive() {
        allUsers.value?.let {
            if (it.isNotEmpty()) {
                userSelected(it[0])
                sharedPrefs.saveLastUserId(it[0].id)
            } else {
                userRepository.addDefaultUser()
            }
        }
    }

}