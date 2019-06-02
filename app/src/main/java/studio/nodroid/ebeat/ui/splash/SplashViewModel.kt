package studio.nodroid.ebeat.ui.splash

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import studio.nodroid.ebeat.model.User
import studio.nodroid.ebeat.room.UserRepository

class SplashViewModel(userRepository: UserRepository) : ViewModel() {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    val requirementsMet = MediatorLiveData<Boolean>().apply { value = false }

    private var userReady: Boolean = false
    private var adSetupDone: Boolean = false

    init {
        requirementsMet.addSource(userRepository.getAllUsers()) {
            if (it == null || it.isEmpty()) {
                scope.launch {
                    userRepository.addUser(User(name = "Default"))
                }
            } else {
                userReady = true
                evaluateConditions()
            }
        }
    }

    fun adSetupDone() {
        adSetupDone = true
        evaluateConditions()
    }

    private fun evaluateConditions() {
        requirementsMet.value = userReady && adSetupDone
    }
}