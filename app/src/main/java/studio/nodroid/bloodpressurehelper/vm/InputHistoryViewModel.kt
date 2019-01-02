package studio.nodroid.bloodpressurehelper.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import studio.nodroid.bloodpressurehelper.model.PressureDataDB
import studio.nodroid.bloodpressurehelper.room.PressureDataRepository
import studio.nodroid.bloodpressurehelper.room.UserRepository
import studio.nodroid.bloodpressurehelper.sharedPrefs.SharedPrefs

class InputHistoryViewModel(
    private val pressureDataRepo: PressureDataRepository,
    private val userRepository: UserRepository,
    private val sharedPrefs: SharedPrefs
) : ViewModel() {

    val selectedUserReadings = MutableLiveData<List<PressureDataDB>>()
}