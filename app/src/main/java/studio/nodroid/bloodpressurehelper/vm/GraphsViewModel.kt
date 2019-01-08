package studio.nodroid.bloodpressurehelper.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import studio.nodroid.bloodpressurehelper.model.Date
import studio.nodroid.bloodpressurehelper.model.DateRange
import studio.nodroid.bloodpressurehelper.model.PressureDataDB
import studio.nodroid.bloodpressurehelper.model.User
import studio.nodroid.bloodpressurehelper.room.PressureDataRepository
import studio.nodroid.bloodpressurehelper.utils.getPeriodTimestamps

class GraphsViewModel constructor(pressureDataRepo: PressureDataRepository) : ViewModel() {

    val allUserReadings = pressureDataRepo.getAllReadings()
    val selectedUserReadings = MutableLiveData<List<PressureDataDB>>()
    val userReadingsForDate = MutableLiveData<List<PressureDataDB>>()
    val selectedDate = MutableLiveData<String>()
    val selectedFilter = MutableLiveData<Int>().apply { value = 0 }

    private var selectedUser: User? = null
    private var selectedRange: DateRange = getPeriodTimestamps(7)

    fun dateSelected(date: Date) {
        selectedDate.value = date.toString()
        selectedRange = getPeriodTimestamps(1, date)
        filterEvents()
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
            selectedUserReadings.value = allUserReadings.value?.filter { id == it.userId }
        }
        if (selectedRange.startStamp == 0L || selectedRange.endStamp == 0L) {
            userReadingsForDate.value = selectedUserReadings.value
        } else {
            rangeSelected(selectedRange)
        }
    }

    fun rangeSelected(periodTimestamps: DateRange) {
        userReadingsForDate.value = selectedUserReadings.value?.filter { it.timestamp in periodTimestamps.startStamp..periodTimestamps.endStamp }
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