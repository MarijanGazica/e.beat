package studio.nodroid.ebeat.ui.flow.actions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import kotlinx.android.synthetic.main.fragment_actions.*
import studio.nodroid.ebeat.R

class ActionsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_actions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        motionLayout.doOnApplyWindowInsets { target, insets, initialState ->
            target.updatePadding(
                bottom = initialState.paddings.bottom + insets.systemWindowInsetBottom
            )
        }

        motionLayout.transitionToState(R.id.visible)

        actionAddReading.setOnClickListener {
            it.findNavController().navigate(R.id.action_actionsFragment_to_readingFragment)
        }

        actionManageUsers.setOnClickListener {
            it.findNavController().navigate(R.id.action_actionsFragment_to_usersFragment)
        }
    }
}