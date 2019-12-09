package studio.nodroid.ebeat.ui.flow.actions

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.transition.TransitionInflater
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import kotlinx.android.synthetic.main.fragment_actions.*
import studio.nodroid.ebeat.R
import studio.nodroid.ebeat.ui.WebViewActivity
import studio.nodroid.ebeat.utils.startDotAnimation

class ActionsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
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

        icon.startDotAnimation()
        icon.transitionName = "dot_animation"

        actionAddReading.setOnClickListener {
            val sharedElements = FragmentNavigatorExtras(
                icon to "dot_animation"
            )
            it.findNavController().navigate(R.id.action_actionsFragment_to_readingFragment, null, null, sharedElements)
        }

        actionManageUsers.setOnClickListener {
            val sharedElements = FragmentNavigatorExtras(
                icon to "dot_animation"
            )
            it.findNavController().navigate(R.id.action_actionsFragment_to_usersFragment, null, null, sharedElements)
        }

        actionViewGraph.setOnClickListener {
            val sharedElements = FragmentNavigatorExtras(
                icon to "dot_animation"
            )
            it.findNavController().navigate(R.id.action_actionsFragment_to_graphsFragment, null, null, sharedElements)
        }

        actionViewReadings.setOnClickListener {
            val sharedElements = FragmentNavigatorExtras(
                icon to "dot_animation"
            )
            it.findNavController().navigate(R.id.action_actionsFragment_to_readingsListFragment, null, null, sharedElements)
        }

        actionEditSettings.setOnClickListener {
            startActivity(Intent(requireContext(), WebViewActivity::class.java))
        }
    }
}