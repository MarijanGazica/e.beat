package studio.nodroid.ebeat.ui.flow.readingsList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import studio.nodroid.ebeat.model.Date
import studio.nodroid.ebeat.model.PressureDataDB
import studio.nodroid.ebeat.model.User
import studio.nodroid.ebeat.room.PressureDataRepository
import studio.nodroid.ebeat.room.UserRepository
import studio.nodroid.ebeat.utils.getPeriodTimestamps
import studio.nodroid.ebeat.utils.toTimestampEnd
import studio.nodroid.ebeat.utils.toTimestampStart

class ReadingsListViewModel(userRepository: UserRepository, private val readingRepo: PressureDataRepository) : ViewModel() {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    val userList: LiveData<List<User>> = userRepository.getAllUsers()
    val readings = MutableLiveData<List<PressureDataDB>>()

    val events: MutableLiveData<Action> = MutableLiveData()

    private var selectedUser: User? = null
    private var startDate: Long? = null

    fun selectedUser(user: User) {
        selectedUser = user
        events.value = Action.SHOW_RANGE_PICKER
    }

    fun allReadingsSelected() {
        scope.launch {
            readings.value = readingRepo.getAllReadingsFor(selectedUser!!.id)
            events.value = Action.SHOW_LIST
        }
    }

    fun time30selected() {
        scope.launch {
            val period = getPeriodTimestamps(30)
            readings.value = readingRepo.getAllReadingsFor(selectedUser!!.id).filter { it.timestamp in period.startStamp..period.endStamp }.sortedBy { it.timestamp }
            events.value = Action.SHOW_LIST
        }
    }

    fun timeRangeSelected() {
        events.value = Action.PICK_RANGE
    }

    fun dateSelected(isInitial: Boolean, chosenDate: Date) {
        if (isInitial) {
            startDate = chosenDate.toTimestampStart()
        } else {
            scope.launch {
                readings.value = readingRepo.getAllReadingsFor(selectedUser!!.id).filter { it.timestamp in startDate!!..chosenDate.toTimestampEnd() }.sortedBy { it.timestamp }
                startDate = null
                events.value = Action.SHOW_LIST
            }
        }
    }

    enum class Action {
        SHOW_USER_PICKER, SHOW_RANGE_PICKER, SHOW_LIST, PICK_RANGE
    }
}