package studio.nodroid.ebeat.ui.flow.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.transition.TransitionInflater
import com.google.android.material.chip.Chip
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import kotlinx.android.synthetic.main.fragment_users.*
import org.koin.android.ext.android.inject
import studio.nodroid.ebeat.R
import studio.nodroid.ebeat.utils.colorFormat
import studio.nodroid.ebeat.utils.hideKeyboard

class UsersFragment : Fragment() {

    private val viewModel by inject<UsersViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        return inflater.inflate(R.layout.fragment_users, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        motionLayout.doOnApplyWindowInsets { target, insets, initialState ->
            target.updatePadding(
                bottom = initialState.paddings.bottom + insets.systemWindowInsetBottom
            )
        }

        motionLayout.transitionToState(R.id.initial)

        viewModel.users.observe(viewLifecycleOwner, Observer { userList ->
            if (userList != null && userList.isNotEmpty()) {
                userChips.removeAllViews()
                userList.forEach { user ->
                    val chip = layoutInflater.inflate(R.layout.item_user, null) as Chip
                    chip.text = user.name
                    chip.setOnClickListener {
                        viewModel.selectedUser(user)
                    }
                    userChips.addView(chip)
                }
            }
            if (userList.size == 1) {
                deleteUser.visibility = View.GONE
            }
        })

        viewModel.state.observe(viewLifecycleOwner, Observer { maybeState ->
            maybeState?.let { state ->
                when (state) {
                    UsersViewModel.State.SelectAction -> motionLayout.transitionToState(R.id.actionQuestion)
                    UsersViewModel.State.NewUser -> motionLayout.transitionToState(R.id.actionAddUser)
                    UsersViewModel.State.DeleteUser.Choose -> motionLayout.transitionToState(R.id.actionDeleteUser)
                    is UsersViewModel.State.DeleteUser.Confirm -> {
                        confirmDeleteQuestion.text = colorFormat(resources.getString(R.string.question_confirm_delete_user), state.name)
                        motionLayout.transitionToState(R.id.actionConfirmDeleteUser)
                    }
                }
            }
        })

        /**
         *
         * Pick action
         *
         */

        createUser.setOnClickListener {
            viewModel.selectedNewUser()
        }

        deleteUser.setOnClickListener {
            viewModel.selectedDeleteUser()
        }

        done.setOnClickListener {
            motionLayout.findNavController().popBackStack()
        }


        /**
         *
         * Add user
         *
         */

        newUserValue.setOnEditorActionListener { v, _, _ ->
            confirmedUserName(v)
            true
        }
        newUserConfirm.setOnClickListener {
            confirmedUserName(it)
        }

        /**
         *
         * Delete user
         *
         */

        abortDeletePick.setOnClickListener {
            viewModel.canceledDeleteUser()
        }

        cancelDeleteUser.setOnClickListener {
            viewModel.canceledDeleteUser()
        }

        confirmDeleteUser.setOnClickListener {
            viewModel.confirmedDeleteUser()
        }

    }

    private fun confirmedUserName(view: View) {
        val enteredText = newUserValue.text.toString()
        if (enteredText.isNotBlank()) {
            viewModel.newUserNameConfirmed(enteredText)
            hideKeyboard(view)
        }
    }

}