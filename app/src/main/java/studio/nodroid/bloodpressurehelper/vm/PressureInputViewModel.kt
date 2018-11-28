package studio.nodroid.bloodpressurehelper.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import studio.nodroid.bloodpressurehelper.model.Date
import studio.nodroid.bloodpressurehelper.model.PressureDataDB
import studio.nodroid.bloodpressurehelper.model.Time
import studio.nodroid.bloodpressurehelper.model.User
import studio.nodroid.bloodpressurehelper.room.PressureDataRepository
import studio.nodroid.bloodpressurehelper.room.UserRepository
import studio.nodroid.bloodpressurehelper.sharedPrefs.SharedPrefs
import studio.nodroid.bloodpressurehelper.utils.timestampFromTime
import java.util.*

class PressureInputViewModel(
    private val userRepository: UserRepository,
    private val pressureDataRepository: PressureDataRepository,
    private val sharedPrefs: SharedPrefs
) : ViewModel() {

    val allUsers = userRepository.getAllUsers()
    val selectedUser = MutableLiveData<User>()
    val selectedTime = MutableLiveData<String>()
    val selectedDate = MutableLiveData<String>()

    private var date: Date
    private var time: Time
    var systolicValue: Int = 0
    var diastolicValue: Int = 0
    var pulseValue: Int = 0
    var description: String = ""

    init {
        val calendar = Calendar.getInstance()
        date = Date(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        time = Time(
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE)
        )
        selectedTime.value = time.toString()
        selectedDate.value = date.toString()
    }

    val userSelected: (User) -> Unit = {
        selectedUser.value = it
        sharedPrefs.saveLastUserId(it.id)
    }

    val timeChosen: (Time) -> Unit = {
        time = it
        selectedTime.value = it.toString()
    }

    val dateChosen: (Date) -> Unit = {
        date = it.copy(month = it.month - 1)
        selectedDate.value = date.toString()
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
                sharedPrefs.saveLastUserId(it[0].id)
            } else {
                userRepository.addDefaultUser()
            }
        }
    }

    fun saveReading() {
        selectedUser.value?.let {
            val reading = PressureDataDB(
                systolic = systolicValue,
                diastolic = diastolicValue,
                pulse = pulseValue,
                timestamp = timestampFromTime(date, time),
                description = description,
                userId = it.id
            )
            pressureDataRepository.addReading(reading)
        }
    }

}