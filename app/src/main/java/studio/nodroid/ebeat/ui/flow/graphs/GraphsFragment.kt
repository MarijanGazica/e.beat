package studio.nodroid.ebeat.ui.flow.graphs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.chip.Chip
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import kotlinx.android.synthetic.main.fragment_graphs.*
import org.koin.android.ext.android.inject
import studio.nodroid.ebeat.R
import studio.nodroid.ebeat.model.User
import studio.nodroid.ebeat.ui.dateTime.DatePickDialog
import studio.nodroid.ebeat.utils.dpPx

class GraphsFragment : Fragment() {

    private val viewModel by inject<GraphsViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_graphs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        motionLayout.doOnApplyWindowInsets { target, insets, initialState ->
            target.updatePadding(
                bottom = initialState.paddings.bottom + insets.systemWindowInsetBottom
            )
        }

        motionLayout.transitionToState(R.id.initial)

        viewModel.userList.observe(viewLifecycleOwner, Observer { list ->
            when (list.size) {
                1 -> viewModel.selectedUser(list[0])
                else -> showUserPicker(list)
            }
        })

        viewModel.events.observe(viewLifecycleOwner, Observer { action ->
            when (action) {
                GraphsViewModel.Action.SHOW_USER_PICKER -> motionLayout.transitionToState(R.id.chooseUser)
                GraphsViewModel.Action.SHOW_RANGE_PICKER -> motionLayout.transitionToState(R.id.chooseRange)
                GraphsViewModel.Action.SHOW_GRAPH -> motionLayout.transitionToState(R.id.graphVisible)
                GraphsViewModel.Action.PICK_RANGE -> showDatePickerDialog()
            }
        })

        timeAll.setOnClickListener { viewModel.allReadingsSelected() }
        timeMonth.setOnClickListener { viewModel.time30selected() }
        timeRange.setOnClickListener { viewModel.timeRangeSelected() }
    }

    private fun showUserPicker(list: List<User>) {
        motionLayout.transitionToState(R.id.chooseUser)
        list.forEach { user ->
            val chip = layoutInflater.inflate(R.layout.item_user, null) as Chip
            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            layoutParams.marginStart = requireContext().dpPx(8f)
            chip.layoutParams = layoutParams
            chip.text = user.name
            chip.setOnClickListener { viewModel.selectedUser(user) }
            chipGroup.addView(chip)
        }
    }

    private fun showDatePickerDialog(which: Int = 0) {
        if (which > 1) return

        DatePickDialog().apply {
            this@apply.onDateChosen = {
                viewModel.dateSelected(which == 0, it)
                showDatePickerDialog(which + 1)
            }
        }.show(childFragmentManager, "")
    }
}