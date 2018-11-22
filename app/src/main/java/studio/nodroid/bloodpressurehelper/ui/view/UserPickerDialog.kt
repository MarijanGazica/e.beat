package studio.nodroid.bloodpressurehelper.ui.view

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
import org.koin.androidx.viewmodel.ext.android.viewModel
import studio.nodroid.bloodpressurehelper.R
import studio.nodroid.bloodpressurehelper.model.User
import studio.nodroid.bloodpressurehelper.room.UserRepository

class UserPickerDialog : DialogFragment() {

    private val userListAdapter by lazy { UserPickerListAdapter(onListItemSelected) }

    private val viewModel: UserPickerViewModel by viewModel()

    var onSelect: (User) -> Unit = {}
    var onAddUserSelected: () -> Unit = {}
    var onEditUsersSelected: () -> Unit = {}

    private val onListItemSelected: (User) -> Unit = {
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

        addUser.setOnClickListener { onAddUserSelected() }
        editUsers.setOnClickListener { onEditUsersSelected() }
    }
}

class UserPickerViewModel(userRepository: UserRepository) : ViewModel() {
    val allUsers = userRepository.getAllUsers()
}

class UserPickerListAdapter(val onListItemSelected: (User) -> Unit) : RecyclerView.Adapter<UserPickerViewHolder>() {

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
    }

}