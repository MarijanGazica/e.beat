package studio.nodroid.ebeat.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import studio.nodroid.ebeat.model.Date
import studio.nodroid.ebeat.model.DateRange
import studio.nodroid.ebeat.model.PressureDataDB
import studio.nodroid.ebeat.model.User
import studio.nodroid.ebeat.room.PressureDataRepository
import studio.nodroid.ebeat.sharedPrefs.AdStatus
import studio.nodroid.ebeat.sharedPrefs.SharedPrefs
import studio.nodroid.ebeat.utils.getPeriodTimestamps
import studio.nodroid.ebeat.utils.toTimestampEnd
import studio.nodroid.ebeat.utils.toTimestampStart

class InputHistoryViewModel(pressureDataRepo: PressureDataRepository, sharedPrefs: SharedPrefs) : ViewModel() {

    val allUserReadings = pressureDataRepo.getAllReadings()
    val selectedUserReadings = MutableLiveData<List<PressureDataDB>>()
    val userReadingsForDate = MutableLiveData<List<PressureDataDB>>()
    val selectedDate = MutableLiveData<String>()
    val selectedFilter = MutableLiveData<Int>().apply { value = 0 }
    val shouldShowDatePicker = MutableLiveData<Boolean>()
    val adStatus = MutableLiveData<AdStatus>().apply { value = sharedPrefs.getAdStatus() }

    private var selectedUser: User? = null
    private var selectedRange: DateRange = getPeriodTimestamps(7)
    private var startDate: Long? = null

    fun dateSelected(date: Date) {
        selectedDate.value = date.toString() // todo
        if (startDate == null) {
            startDate = date.toTimestampStart()
            shouldShowDatePicker.value = true
            shouldShowDatePicker.value = false
            return
        }
        startDate?.let {
            val endStamp = date.toTimestampEnd()
            selectedRange = if (endStamp > it) {
                DateRange(it, date.toTimestampEnd())
            } else {
                DateRange(date.toTimestampEnd(), it)
            }
            filterEvents()
        }
    }

    fun userSelected(user: User?) {
        selectedUser = user
        filterEvents()
    }

    fun readingsReady() {
        filterEvents()
    }

    private fun filterEvents() {
        selectedUser?.run {
            selectedUserReadings.value = allUserReadings.value?.filter { id == it.userId }?.sortedBy { it.timestamp }
        }
        if (selectedRange.startStamp == 0L || selectedRange.endStamp == 0L) {
            userReadingsForDate.value = selectedUserReadings.value
        } else {
            setReadingsForRange(selectedRange)
        }
    }

    private fun setReadingsForRange(periodTimestamps: DateRange) {
        userReadingsForDate.value = selectedUserReadings.value?.filter { it.timestamp in periodTimestamps.startStamp..periodTimestamps.endStamp }
    }

    fun weekSelected() {
        val period = getPeriodTimestamps(7)
        setReadingsForRange(period)
    }

    fun monthSelected() {
        val period = getPeriodTimestamps(30)
        setReadingsForRange(period)
    }

    fun rangeSelected() {
        startDate = null
        shouldShowDatePicker.value = true
        shouldShowDatePicker.value = false
    }

    fun allTimeSelected() {
        selectedRange = DateRange(0, 0)
        userReadingsForDate.value = selectedUserReadings.value
    }

    fun filterSelected(position: Int) {
        selectedFilter.value = position
        if (position != 3) {
            selectedDate.value = null
        }
    }
}