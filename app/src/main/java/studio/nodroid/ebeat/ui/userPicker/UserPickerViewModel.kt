package studio.nodroid.ebeat.ui.userPicker

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import studio.nodroid.ebeat.analytics.Analytics
import studio.nodroid.ebeat.analytics.AnalyticsEvent
import studio.nodroid.ebeat.model.User
import studio.nodroid.ebeat.room.UserRepository
import studio.nodroid.ebeat.sharedPrefs.SharedPrefs

class UserPickerViewModel(
    userRepository: UserRepository,
    private val sharedPrefs: SharedPrefs,
    private val analytics: Analytics
) : ViewModel() {

    val allUsers = userRepository.getAllUsers()

    val activeUser = MutableLiveData<User>()

    val onUserSelected: (User) -> Unit = {
        analytics.logEvent(AnalyticsEvent.USER_CHANGE)

        activeUser.value = allUsers.value?.firstOrNull { item -> item.id == sharedPrefs.getLastUserId() }
    }

    fun usersAvailable() {
        activeUser.value = allUsers.value?.firstOrNull {
            it.id == sharedPrefs.getLastUserId()
        }

        if (activeUser.value == null) {
            val user = allUsers.value?.firstOrNull()
            user?.run {
                sharedPrefs.saveLastUserId(id)
                onUserSelected(this)
            }
        }
    }

}
