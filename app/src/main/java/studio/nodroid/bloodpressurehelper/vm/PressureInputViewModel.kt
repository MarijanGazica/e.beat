package studio.nodroid.bloodpressurehelper.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import studio.nodroid.bloodpressurehelper.model.PressureData
import studio.nodroid.bloodpressurehelper.model.PressureDataDB
import studio.nodroid.bloodpressurehelper.model.PressureInfo
import studio.nodroid.bloodpressurehelper.model.User
import studio.nodroid.bloodpressurehelper.room.PressureDataRepository
import studio.nodroid.bloodpressurehelper.room.UserRepository
import studio.nodroid.bloodpressurehelper.sharedPrefs.SharedPrefs

class PressureInputViewModel(
    private val userRepository: UserRepository,
    private val pressureDataRepository: PressureDataRepository,
    private val sharedPrefs: SharedPrefs
) : ViewModel() {

    val allUsers = userRepository.getAllUsers()
    val selectedUser = MutableLiveData<User>()
    val lastReading = MutableLiveData<PressureInfo>()

    init {
        loadLastReading()
    }

    val userSelected: (User) -> Unit = {
        selectedUser.value = it
        sharedPrefs.saveLastUserId(it.id)
        loadLastReading()
    }

    fun findLastUser() {
        val id = sharedPrefs.getLastUserId()
        val lastUser = allUsers.value?.firstOrNull { it.id == id }
        lastUser?.run {
            selectedUser.value = this
            loadLastReading()
        } ?: setFirstUserActive()
    }

    private fun setFirstUserActive() {
        allUsers.value?.let {
            if (it.isNotEmpty()) {
                selectedUser.value = it[0]
                sharedPrefs.saveLastUserId(it[0].id)
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
                    timestamp = timestamp,
                    description = description,
                    userId = it.id
                )
                pressureDataRepository.addReading(reading)
            }
        }
    }

    private fun loadLastReading() {
        selectedUser.value?.let {
            GlobalScope.launch(Dispatchers.Main) {
                val readingList = pressureDataRepository.getReadingsForUser(it.id)
                if (readingList.isEmpty()) {
                    lastReading.value = PressureInfo(130, 60, 60)
                } else {
                    val latest = readingList.sortedBy { value -> value.timestamp }.last()
                    lastReading.value = PressureInfo(latest.systolic, latest.diastolic, latest.pulse)
                }
            }
        }
    }

}