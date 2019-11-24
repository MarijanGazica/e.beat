package studio.nodroid.ebeat.ui.flow.graphs

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

class GraphsViewModel(userRepository: UserRepository, private val readingRepo: PressureDataRepository) : ViewModel() {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    val userList: LiveData<List<User>> = userRepository.getAllUsers()
    val readings = MutableLiveData<List<PressureDataDB>>()

    val events: MutableLiveData<Action> = MutableLiveData()

    private var selectedUser: User? = null
    private var firstDate: Date? = null

//    init {
//        scope.launch {
//            userRepository.getAllUsersList().forEach { Log.d("findme", it.toString()) }
//        }
    /*
    scope.launch {
        for (i in 0..180) {
            val sys = Random.nextInt(115, 125)
            val dia = Random.nextInt(65, 75)
            val puls = Random.nextInt(60, 80)
            val reading = PressureDataDB(systolic = sys, diastolic = dia, pulse = puls, timestamp = System.currentTimeMillis() - i * DAY, userId = 1, description = "Whatever")
            readingRepo.addReading(reading).join()
        }
    }
    */
//    }

    fun selectedUser(user: User) {
        selectedUser = user
        events.value = Action.ShowRangePicker
    }

    fun allReadingsSelected() {
        scope.launch {
            readings.value = readingRepo.getAllReadingsFor(selectedUser!!.id)
            events.value = Action.ShowReadingGraph(readings.value?.isEmpty() ?: true)
        }
    }

    fun time30selected() {
        scope.launch {
            val period = getPeriodTimestamps(30)
            readings.value = readingRepo.getAllReadingsFor(selectedUser!!.id).filter { it.timestamp in period.startStamp..period.endStamp }.sortedBy { it.timestamp }
            events.value = Action.ShowReadingGraph(readings.value?.isEmpty() ?: true)
        }
    }

    fun timeRangeSelected() {
        events.value = Action.ShowRangeDialog
    }

    fun dateSelected(isInitial: Boolean, chosenDate: Date) {
        if (isInitial) {
            firstDate = chosenDate
        } else {
            scope.launch {
                val dateRange = if (firstDate!!.before(chosenDate)) {
                    Pair(firstDate!!.toTimestampStart(), chosenDate.toTimestampEnd())
                } else {
                    Pair(chosenDate.toTimestampStart(), firstDate!!.toTimestampEnd())
                }
                readings.value = readingRepo.getAllReadingsFor(selectedUser!!.id).filter { it.timestamp in dateRange.first..dateRange.second }.sortedBy { it.timestamp }
                firstDate = null
                events.value = Action.ShowReadingGraph(readings.value?.isEmpty() ?: true)
            }
        }
    }

    fun changeSelectionSelected() {
        firstDate = null
        if (userList.value!!.size > 1) {
            selectedUser = null
            events.value = Action.ShowUserPicker
        } else {
            events.value = Action.ShowRangePicker
        }
    }

    sealed class Action {
        object ShowUserPicker : Action()
        object ShowRangePicker : Action()
        data class ShowReadingGraph(val isEmpty: Boolean) : Action()
        object ShowRangeDialog : Action()
    }
}