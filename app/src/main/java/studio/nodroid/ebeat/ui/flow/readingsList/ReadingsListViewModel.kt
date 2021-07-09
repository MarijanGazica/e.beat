package studio.nodroid.ebeat.ui.flow.readingsList

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import studio.nodroid.ebeat.analytics.Analytics
import studio.nodroid.ebeat.analytics.AnalyticsEvent
import studio.nodroid.ebeat.model.Date
import studio.nodroid.ebeat.model.PressureDataDB
import studio.nodroid.ebeat.model.User
import studio.nodroid.ebeat.room.PressureDataRepository
import studio.nodroid.ebeat.room.UserRepository
import studio.nodroid.ebeat.utils.getPeriodTimestamps
import studio.nodroid.ebeat.utils.toTimestampEnd
import studio.nodroid.ebeat.utils.toTimestampStart

class ReadingsListViewModel(
    userRepository: UserRepository,
    private val readingRepo: PressureDataRepository,
    private val analytics: Analytics
) : ViewModel() {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    val userList: LiveData<List<User>> = userRepository.getAllUsers()
    val readings = MutableLiveData<List<PressureDataDB>>()
    val events: MutableLiveData<Action> = MutableLiveData()

    private var selectedUser: User? = null
    private var firstDate: Date? = null

    fun selectedUser(user: User) {
        selectedUser = user
        events.value = Action.ShowRangePicker
    }

    fun allReadingsSelected() {
        periodSelected("all")
        scope.launch {
            readings.value = readingRepo.getAllReadingsFor(selectedUser!!.id)
            events.value = Action.ShowReadingList(readings.value?.isEmpty() ?: true)
        }
    }

    fun time30selected() {
        periodSelected("month")
        scope.launch {
            val period = getPeriodTimestamps(30)
            readings.value = readingRepo.getAllReadingsFor(selectedUser!!.id).filter { it.timestamp in period.startStamp..period.endStamp }.sortedBy { it.timestamp }
            events.value = Action.ShowReadingList(readings.value?.isEmpty() ?: true)
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
                periodSelected("custom")
                events.value = Action.ShowReadingList(readings.value?.isEmpty() ?: true)
            }
        }
    }

    fun selectedChangeSelection() {
        userList.value?.run {
            if (size > 1) {
                events.value = Action.ShowUserPicker
            } else {
                events.value = Action.ShowRangePicker
            }
        }
    }

    private fun periodSelected(period: String) {
        val analyticsBundle = Bundle()
        analyticsBundle.putString("period", period)
        analytics.logEvent(AnalyticsEvent.VIEWED_LIST, analyticsBundle)
    }

    sealed class Action {
        object ShowUserPicker : Action()
        object ShowRangePicker : Action()
        data class ShowReadingList(val isEmpty: Boolean) : Action()
        object ShowRangeDialog : Action()
    }
}