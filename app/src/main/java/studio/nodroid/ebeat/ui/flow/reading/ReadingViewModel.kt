package studio.nodroid.ebeat.ui.flow.reading

import android.os.Bundle
import androidx.lifecycle.*
import kotlinx.coroutines.*
import studio.nodroid.ebeat.analytics.Analytics
import studio.nodroid.ebeat.analytics.AnalyticsEvent
import studio.nodroid.ebeat.model.*
import studio.nodroid.ebeat.room.PressureDataRepository
import studio.nodroid.ebeat.room.UserRepository
import studio.nodroid.ebeat.time.TimeProvider
import studio.nodroid.ebeat.utils.SingleLiveEvent
import studio.nodroid.ebeat.utils.getPressureRating
import studio.nodroid.ebeat.utils.timestampFromTime

class ReadingViewModel(
    userRepository: UserRepository,
    private val pressureRepo: PressureDataRepository,
    private val timeProvider: TimeProvider,
    private val analytics: Analytics
) : ViewModel() {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    val userList: LiveData<List<User>> = userRepository.getAllUsers()
    val selectedUser = MutableLiveData<User>()
    val selectedTime = MutableLiveData<Long>()
    val selectedSystolic = MutableLiveData<Int>()
    val selectedDiastolic = MutableLiveData<Int>()
    val selectedPulse = MutableLiveData<Int>()
    val selectedDescription = MutableLiveData<String>()
    val events = SingleLiveEvent<State>()
    val readingSeverity = MediatorLiveData<PressureSeverity>()

    private var tempDate: Date? = null
    private var timer: Job? = null

    private val analyticsBundle = Bundle()

    private val severityCalc = Observer<Int> { readingSeverity.value = getPressureRating(selectedSystolic.value, selectedDiastolic.value) }

    init {
        readingSeverity.addSource(selectedSystolic, severityCalc)
        readingSeverity.addSource(selectedDiastolic, severityCalc)
    }

    fun selectedUser(user: User) {
        selectedUser.value = user
    }

    fun readingTakenNow() {
        selectedTime.value = timeProvider.getCurrentTime()
        analyticsBundle.putString("time", "now")
    }

    fun timeNotNowSelected() {
        analyticsBundle.putString("time", "custom")
        events.value = State.DateNeeded
    }

    fun timeSelected(time: Time) {
        tempDate?.let {
            selectedTime.value = timestampFromTime(it, time)
            tempDate = null
        }
    }

    fun dateSelected(date: Date) {
        tempDate = date
        events.value = State.TimeNeeded
    }

    fun systolicPressureEntered(value: String) {
        try {
            selectedSystolic.value = value.toInt()
        } catch (e: NumberFormatException) {
            //noop
        }
    }

    fun diastolicPressureEntered(value: String) {
        try {
            selectedDiastolic.value = value.toInt()
        } catch (e: NumberFormatException) {
            //noop
        }
    }

    fun pulseEntered(value: String) {
        try {
            selectedPulse.value = value.toInt()
        } catch (e: NumberFormatException) {
            //noop
        }
    }

    fun descriptionEntered(description: String) {
        selectedDescription.value = description
    }

    fun saveReading() {
        scope.launch {
            pressureRepo.addReading(
                PressureDataDB(
                    systolic = selectedSystolic.value!!,
                    diastolic = selectedDiastolic.value!!,
                    pulse = selectedPulse.value!!,
                    timestamp = selectedTime.value!!,
                    description = selectedDescription.value,
                    userId = selectedUser.value!!.id
                )
            ).join()
            events.value = State.Saved
            analyticsBundle.putString("description", (!selectedDescription.value.isNullOrBlank()).toString())
            analyticsBundle.putString("severity", readingSeverity.value?.name)
            analytics.logEvent(AnalyticsEvent.READING_ADDED, analyticsBundle)
        }

        timer = scope.launch {
            withContext(Dispatchers.IO) {
                delay(3000)
            }
            events.value = State.Done
        }
    }

    fun doRecord(sys: Int, dia: Int, p: Int, t: Long, De: String) {
        scope.launch {
            pressureRepo.addReading(
                PressureDataDB(
                    systolic = sys,
                    diastolic = dia,
                    pulse = p,
                    timestamp = t,
                    description = De,
                    userId = 8
                )
            )
        }
    }

    fun discardReading() {
        events.value = State.AskDiscard(selectedDescription.value?.isNotEmpty() ?: false)
    }

    fun savedNotificationDismissed() {
        timer?.cancel()
    }

    fun confirmedDiscard() {
        analytics.logEvent(AnalyticsEvent.READING_DISCARDED)
        events.value = State.Done
    }

    fun abortedDiscard() {
        events.value = State.FinishedDiscard(selectedDescription.value?.isNotEmpty() ?: false)
    }

    sealed class State {
        object TimeNeeded : State()
        object DateNeeded : State()
        object Saved : State()
        object Done : State()
        data class AskDiscard(val isDescriptionSet: Boolean) : State()
        data class FinishedDiscard(val isDescriptionSet: Boolean) : State()
    }

}

data class ReadingData(val user: User? = null, val time: Long? = null, val systolic: Int? = null, val diastolic: Int? = null, val pulse: Int? = null)
