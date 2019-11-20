package studio.nodroid.ebeat.ui.flow.readingsList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import androidx.transition.TransitionInflater
import com.google.android.material.chip.Chip
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import kotlinx.android.synthetic.main.fragment_graphs.icon
import kotlinx.android.synthetic.main.fragment_readings_list.*
import org.koin.android.ext.android.inject
import studio.nodroid.ebeat.R
import studio.nodroid.ebeat.model.PressureDataDB
import studio.nodroid.ebeat.model.User
import studio.nodroid.ebeat.ui.dateTime.DatePickDialog
import studio.nodroid.ebeat.ui.inputHistory.ReadingHistoryAdapter
import studio.nodroid.ebeat.utils.dpPx
import studio.nodroid.ebeat.utils.startDotAnimation


class ReadingsListFragment : Fragment() {

    private val viewModel by inject<ReadingsListViewModel>()

    private val readingHistoryAdapter by lazy { ReadingHistoryAdapter(onReadingSelected) }

    private val onReadingSelected: (PressureDataDB) -> Unit = {
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        return inflater.inflate(R.layout.fragment_readings_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        motionLayout.doOnApplyWindowInsets { target, insets, initialState ->
            target.updatePadding(
                bottom = initialState.paddings.bottom + insets.systemWindowInsetBottom
            )
        }
        readingList.doOnApplyWindowInsets { view, insets, initialState ->
            view.updatePadding(
                top = initialState.paddings.top + insets.systemWindowInsetTop
            )
        }

        icon.startDotAnimation()

        motionLayout.transitionToState(R.id.initial)

        readingList.layoutManager = LinearLayoutManager(requireContext())
        val decoration = DividerItemDecoration(requireContext(), VERTICAL)
        readingList.addItemDecoration(decoration)
        readingList.adapter = readingHistoryAdapter

        viewModel.userList.observe(viewLifecycleOwner, Observer { list ->
            when (list.size) {
                1 -> viewModel.selectedUser(list[0])
                else -> showUserPicker(list)
            }
        })

        viewModel.events.observe(viewLifecycleOwner, Observer { action ->
            when (action) {
                ReadingsListViewModel.Action.SHOW_USER_PICKER -> motionLayout.transitionToState(R.id.chooseUser)
                ReadingsListViewModel.Action.SHOW_RANGE_PICKER -> motionLayout.transitionToState(R.id.chooseRange)
                ReadingsListViewModel.Action.SHOW_LIST -> motionLayout.transitionToState(R.id.listVisible)
                ReadingsListViewModel.Action.PICK_RANGE -> showDatePickerDialog()
            }
        })

        viewModel.readings.observe(viewLifecycleOwner, Observer {
            it?.sortedByDescending { item -> item.timestamp }
                ?.run { readingHistoryAdapter.setData(this) }
            // todo handle empty
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