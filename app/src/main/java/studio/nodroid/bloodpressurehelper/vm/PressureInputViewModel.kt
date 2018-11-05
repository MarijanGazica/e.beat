package studio.nodroid.bloodpressurehelper.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import studio.nodroid.bloodpressurehelper.model.PressureData
import studio.nodroid.bloodpressurehelper.model.PressureDataDB
import studio.nodroid.bloodpressurehelper.model.User
import studio.nodroid.bloodpressurehelper.room.PressureDataRepository
import studio.nodroid.bloodpressurehelper.room.UserRepository
import studio.nodroid.bloodpressurehelper.sharedPrefs.SharedPrefs

class PressureInputViewModel(
    private val userRepository: UserRepository,
    val pressureDataRepository: PressureDataRepository,
    private val sharedPrefs: SharedPrefs
) : ViewModel() {

    val allUsers = userRepository.getAllUsers()
    val selectedUser = MutableLiveData<User>()

    val userSelected: (User) -> Unit = {
        selectedUser.value = it
    }

    fun findLastUser() {
        val id = sharedPrefs.getLastUserId()
        val lastUser = allUsers.value?.firstOrNull { it.id == id }
        lastUser?.run {
            selectedUser.value = this
        } ?: setFirstUserActive()
    }

    private fun setFirstUserActive() {
        allUsers.value?.let {
            if (it.isNotEmpty()) {
                selectedUser.value = it[0]
            } else {
                userRepository.addDefaultUser()
            }
        }
    }

    fun saveReading(value: PressureData?) {
        selectedUser.value?.let {
            value?.run {
                val reading = PressureDataDB(
                    systolic = systolic,
                    diastolic = diastolic,
                    pulse = pulse,
                    weight = it.weight,
                    timestamp = System.currentTimeMillis(),
                    description = description,
                    userId = it.id
                )
                pressureDataRepository.addReading(reading)
            }
        }
    }

}