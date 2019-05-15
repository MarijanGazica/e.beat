package studio.nodroid.ebeat.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import studio.nodroid.ebeat.analytics.Analytics
import studio.nodroid.ebeat.analytics.AnalyticsEvent
import studio.nodroid.ebeat.model.*
import studio.nodroid.ebeat.model.Date
import studio.nodroid.ebeat.room.PressureDataRepository
import studio.nodroid.ebeat.sharedPrefs.AdStatus
import studio.nodroid.ebeat.sharedPrefs.SharedPrefs
import studio.nodroid.ebeat.utils.getPressureRating
import studio.nodroid.ebeat.utils.timestampFromTime
import java.util.*

class PressureInputViewModel(
    private val pressureDataRepository: PressureDataRepository,
    private val analytics: Analytics,
    sharedPrefs: SharedPrefs
) : ViewModel() {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    var selectedUser: User? = null
    val selectedTime = MutableLiveData<String>()
    val selectedDate = MutableLiveData<String>()
    val pressureSeverity = MutableLiveData<PressureSeverity>().apply {
        value = PressureSeverity.AWAITING_INPUT
    }
    val adStatus = MutableLiveData<AdStatus>().apply { value = sharedPrefs.getAdStatus() }

    val screenState = MutableLiveData<ScreenState>()

    private var date: Date
    private var time: Time
    private var systolicValue: Int = -1
    private var diastolicValue: Int = -1
    private var pulseValue: Int = 0
    private var description: String = ""

    init {
        val calendar = Calendar.getInstance()
        date = Date(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        time = Time(
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE)
        )
        selectedTime.value = time.toString()
        selectedDate.value = date.toString()
    }

    val timeChosen: (Time) -> Unit = {
        time = it
        selectedTime.value = it.toString()
    }

    val dateChosen: (Date) -> Unit = {
        date = it.copy(month = it.month)
        selectedDate.value = date.toString()
    }

    fun setSystolicValue(value: Int) {
        systolicValue = value
        updatePressureSeverity()
    }

    fun setDiastolicValue(value: Int) {
        diastolicValue = value
        updatePressureSeverity()
    }

    fun setPulseValue(value: Int) {
        pulseValue = value
    }

    fun setDescription(value: String) {
        description = value
    }

    private fun updatePressureSeverity() {
        pressureSeverity.value = getPressureRating(systolicValue, diastolicValue)
    }

    fun saveReading() {
        if (systolicValue < 1 || diastolicValue < 1 || pulseValue < 1 || diastolicValue >= systolicValue) {
            screenState.value = ScreenState.INPUT_MISSING
            screenState.value = ScreenState.IDLE
            return
        }

        selectedUser?.let {
            analytics.logEvent(AnalyticsEvent.READING_ADD)

            if (description.isNotBlank()) {
                analytics.logEvent(AnalyticsEvent.READING_ADD)
            }

            val reading = PressureDataDB(
                systolic = systolicValue,
                diastolic = diastolicValue,
                pulse = pulseValue,
                timestamp = timestampFromTime(date, time),
                description = description,
                userId = it.id
            )

            scope.launch {
                pressureDataRepository.addReading(reading)
            }
            screenState.value = ScreenState.READING_SAVED
        }
    }

    fun saveReadingPressed() {
        if (systolicValue < 1 || diastolicValue < 1 || pulseValue < 1 || diastolicValue >= systolicValue) {
            screenState.value = ScreenState.INPUT_MISSING
        } else {
            screenState.value = ScreenState.DATA_OK
        }
        screenState.value = ScreenState.IDLE

    }

    enum class ScreenState {
        IDLE, INPUT_MISSING, READING_SAVED, DATA_OK
    }
}