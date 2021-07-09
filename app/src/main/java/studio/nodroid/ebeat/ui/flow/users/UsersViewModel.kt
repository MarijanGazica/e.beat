package studio.nodroid.ebeat.ui.flow.users

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import studio.nodroid.ebeat.analytics.Analytics
import studio.nodroid.ebeat.analytics.AnalyticsEvent
import studio.nodroid.ebeat.model.User
import studio.nodroid.ebeat.room.UserRepository
import studio.nodroid.ebeat.utils.SingleLiveEvent

class UsersViewModel(
    private val userRepo: UserRepository,
    private val analytics: Analytics
) : ViewModel() {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    val users = userRepo.getAllUsers()
    val state = SingleLiveEvent<State>().apply { value = State.SelectAction }
    private var markedUser: User? = null

    fun newUserNameConfirmed(name: String) {
        analytics.logEvent(AnalyticsEvent.USER_ADDED)
        scope.launch {
            userRepo.addUser(User(name = name))
            state.value = State.SelectAction
        }
    }

    fun selectedNewUser() {
        state.value = State.NewUser
    }

    fun selectedDeleteUser() {
        state.value = State.DeleteUser.Choose
    }

    fun canceledDeleteUser() {
        markedUser = null
        state.value = State.SelectAction
    }

    fun selectedUser(user: User) {
        if (state.value == State.DeleteUser.Choose) {
            markedUser = user
            state.value = State.DeleteUser.Confirm(user.name)
        }
    }

    fun confirmedDeleteUser() {
        analytics.logEvent(AnalyticsEvent.USER_DELETED)
        markedUser?.let { user ->
            scope.launch {
                userRepo.deleteUser(user)
                markedUser = null
                state.value = State.SelectAction
            }
        }
    }

    sealed class State {
        object SelectAction : State()
        object NewUser : State()
        sealed class DeleteUser : State() {
            object Choose : DeleteUser()
            data class Confirm(val name: String) : DeleteUser()
        }
    }
}