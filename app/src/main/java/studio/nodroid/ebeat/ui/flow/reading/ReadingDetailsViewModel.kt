package studio.nodroid.ebeat.ui.flow.reading

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import studio.nodroid.ebeat.model.Date
import studio.nodroid.ebeat.model.Time
import studio.nodroid.ebeat.model.User
import studio.nodroid.ebeat.room.UserRepository
import studio.nodroid.ebeat.time.TimeProvider
import studio.nodroid.ebeat.utils.SingleLiveEvent
import studio.nodroid.ebeat.utils.timestampFromTime

class ReadingDetailsViewModel(userRepository: UserRepository, private val timeProvider: TimeProvider) : ViewModel() {

    val userList: LiveData<List<User>> = userRepository.getAllUsers()
    val selectedUser = MutableLiveData<User>()
    val selectedTime = MutableLiveData<Long>()
    val events = SingleLiveEvent<Action>()

    private var tempDate: Date? = null

    fun selectedUser(user: User) {
        selectedUser.value = user
    }

    fun readingTakenNow() {
        selectedTime.value = timeProvider.getCurrentTime()
    }

    fun timeNotNowSelected() {
        events.value = Action.DATE_NEEDED
    }

    fun timeSelected(time: Time) {
        tempDate?.let {
            selectedTime.value = timestampFromTime(it, time)
            tempDate = null
        }
    }

    fun dateSelected(date: Date) {
        tempDate = date
        events.value = Action.TIME_NEEDED
    }

    enum class Action {
        TIME_NEEDED, DATE_NEEDED
    }

}

data class ReadingData(val user: User? = null, val time: Long)
