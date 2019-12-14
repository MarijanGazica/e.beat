package studio.nodroid.ebeat.ui.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import studio.nodroid.ebeat.model.User
import studio.nodroid.ebeat.room.UserRepository
import studio.nodroid.ebeat.sharedPrefs.SharedPrefs

class SplashViewModel(
    userRepository: UserRepository,
    private val sharedPrefs: SharedPrefs
) : ViewModel() {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    val requirementsMet = MutableLiveData<Event>()

    init {
        scope.launch {
            val userList = userRepository.getAllUsersList()
            when {
                userList.isNullOrEmpty() -> {
                    userRepository.addUser(User(name = "Default")).join()
                    requirementsMet.value = Event.SHOW_MAIN
                }
                sharedPrefs.shouldShowFlowUpdateWelcome() -> requirementsMet.value = Event.SHOW_FLOW_UPDATE_WELCOME
                else -> requirementsMet.value = Event.SHOW_MAIN
            }
        }
    }

    enum class Event {
        SHOW_MAIN, SHOW_FLOW_UPDATE_WELCOME
    }
}