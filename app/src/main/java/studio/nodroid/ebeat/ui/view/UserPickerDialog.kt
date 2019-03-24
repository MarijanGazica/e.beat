package studio.nodroid.ebeat.ui.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.dialog_user_picker.*
import kotlinx.android.synthetic.main.dialog_user_picker.view.*
import kotlinx.android.synthetic.main.item_user_picker.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import studio.nodroid.ebeat.R
import studio.nodroid.ebeat.analytics.Analytics
import studio.nodroid.ebeat.analytics.AnalyticsEvent
import studio.nodroid.ebeat.model.User
import studio.nodroid.ebeat.room.UserRepository
import studio.nodroid.ebeat.sharedPrefs.SharedPrefs
import studio.nodroid.ebeat.utils.hideKeyboard
import studio.nodroid.ebeat.utils.onTextChanged
import studio.nodroid.ebeat.utils.showKeyboard

class UserPickerDialog : DialogFragment() {

    private val userListAdapter by lazy { UserPickerListAdapter(onListItemSelected) }

    private val viewModel: UserListViewModel by viewModel()
    private val sharedPrefs: SharedPrefs by inject()

    var onSelect: (User) -> Unit = {}

    private val onListItemSelected: (User) -> Unit = {
        sharedPrefs.saveLastUserId(it.id)
        onSelect(it)
        dismiss()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        viewModel.allUsers.observe(this, Observer { userListAdapter.setData(it) })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_user_picker, container, false)
        view.userList.layoutManager = LinearLayoutManager(requireContext())
        view.userList.adapter = userListAdapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addUser.setOnClickListener {
            addUserContainer.visibility = View.VISIBLE
            addUser.visibility = View.GONE
            showKeyboard(userText)
        }
        cancelAdd.setOnClickListener {
            addUserContainer.visibility = View.GONE
            addUser.visibility = View.VISIBLE
            userText.text?.clear()
            hideKeyboard(userText)
        }
        userText.onTextChanged {
            if (it.isNotEmpty()) {
                saveUser.alpha = 1f
            } else {
                saveUser.alpha = 0.5f
            }
        }
        saveUser.setOnClickListener {
            userText.text?.run {
                if (this.isNotBlank()) {
                    viewModel.addUser(userText.text.toString())
                    userText.text?.clear()
                    addUserContainer.visibility = View.GONE
                    addUser.visibility = View.VISIBLE
                    hideKeyboard(userText)
                }
            }
        }

    }

}

class UserListViewModel(private val userRepository: UserRepository, private val analytics: Analytics) : ViewModel() {

    val allUsers = userRepository.getAllUsers()

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    fun addUser(name: String) {
        analytics.logEvent(AnalyticsEvent.USER_ADD)

        scope.launch {
            userRepository.addUser(User(name = name))
        }
    }
}

class UserPickerListAdapter(private val onListItemSelected: (User) -> Unit) : RecyclerView.Adapter<UserPickerViewHolder>() {

    private val data = mutableListOf<User>()

    fun setData(users: List<User>) {
        data.clear()
        data.addAll(users)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserPickerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_picker, parent, false)
        return UserPickerViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: UserPickerViewHolder, position: Int) {
        holder.bind(data[position], onListItemSelected)
    }
}

class UserPickerViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    fun bind(user: User, onListItemSelected: (User) -> Unit) {
        view.userName.text = user.name
        view.setOnClickListener { onListItemSelected(user) }

        val drawable = when (user.id % 5) {
            0 -> R.drawable.ic_pattern_blue
            1 -> R.drawable.ic_pattern_yellow
            2 -> R.drawable.ic_pattern_green
            3 -> R.drawable.ic_pattern_purple
            else -> R.drawable.ic_pattern_salmon
        }

        view.userName.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0)
    }

}