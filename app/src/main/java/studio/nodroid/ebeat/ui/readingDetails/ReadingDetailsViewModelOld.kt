package studio.nodroid.ebeat.ui.readingDetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import studio.nodroid.ebeat.model.PressureDataDB
import studio.nodroid.ebeat.room.PressureDataRepository

class ReadingDetailsViewModelOld(private val pressureRepo: PressureDataRepository) : ViewModel() {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    val selectedReading = MutableLiveData<PressureDataDB>()

    fun readingSelected(id: Int) {
        scope.launch {
            selectedReading.value = pressureRepo.getReadingById(id)
        }
    }

    fun deleteReading() {
        selectedReading.value?.let {
            scope.launch {
                pressureRepo.deleteReading(it).join()
                selectedReading.value = null
            }
        }
    }
}