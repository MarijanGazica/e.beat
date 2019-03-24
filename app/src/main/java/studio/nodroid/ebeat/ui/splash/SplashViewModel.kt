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
    private val scope = CoroutineScope(Dispatchers.Default + job)

    val userReady = MediatorLiveData<Boolean>().apply { value = false }

    init {
        userReady.addSource(userRepository.getAllUsers()) {
            if (it == null || it.isEmpty()) {
                scope.launch {
                    userRepository.addUser(User(name = "Default"))
                }
            } else {
                userReady.value = true
            }
        }
    }

}