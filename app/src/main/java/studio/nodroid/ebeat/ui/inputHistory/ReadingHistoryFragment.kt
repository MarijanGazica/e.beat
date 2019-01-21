package studio.nodroid.ebeat.ui.inputHistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_input_history.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import studio.nodroid.ebeat.R
import studio.nodroid.ebeat.ui.view.DatePickerView
import studio.nodroid.ebeat.ui.view.FilterView
import studio.nodroid.ebeat.vm.InputHistoryViewModel
import studio.nodroid.ebeat.vm.UserPickerViewModel


class ReadingHistoryFragment : Fragment() {

    private val viewModel: InputHistoryViewModel by viewModel()
    private val userViewModel: UserPickerViewModel by sharedViewModel()
    private val readingHistoryAdapter by lazy { ReadingHistoryAdapter() }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_input_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inputList.layoutManager = LinearLayoutManager(requireContext())
        inputList.adapter = readingHistoryAdapter

        filters.onFilterSelected {
            when (it) {
                FilterView.Selection.WEEK -> {
                    viewModel.weekSelected()
                    viewModel.filterSelected(0)
                }
                FilterView.Selection.MONTH -> {
                    viewModel.monthSelected()
                    viewModel.filterSelected(1)
                }
                FilterView.Selection.ALL_TIME -> {
                    viewModel.allTimeSelected()
                    viewModel.filterSelected(2)
                }
                FilterView.Selection.RANGE -> {
                    viewModel.rangeSelected()
                }
            }
        }

        userViewModel.activeUser.observe(viewLifecycleOwner, Observer { viewModel.userSelected(it) })

        viewModel.selectedUserReadings.observe(viewLifecycleOwner, Observer {})
        viewModel.allUserReadings.observe(viewLifecycleOwner, Observer { viewModel.readingsReady() })
        viewModel.selectedDate.observe(viewLifecycleOwner, Observer { /* filters.setDateText(it ?: resources.getString(R.string.range))*/ })
        viewModel.selectedFilter.observe(viewLifecycleOwner, Observer { it?.run { filters.markSelection(this) } })

        viewModel.userReadingsForDate.observe(viewLifecycleOwner, Observer {
            it?.run {
                readingHistoryAdapter.setData(this)
            }
            if (it == null || it.isEmpty()) {
                emptyListGroup.visibility = View.VISIBLE
            } else {
                emptyListGroup.visibility = View.GONE
            }
        })

        viewModel.shouldShowDatePicker.observe(viewLifecycleOwner, Observer {
            it?.let { shouldShowFragment ->
                if (shouldShowFragment) {
                    showDatePickerDialog()
                }
            }
        })
    }

    private fun showDatePickerDialog() {
        DatePickerView().apply {
            this@apply.onDateChosen = {
                viewModel.dateSelected(it)
                viewModel.filterSelected(3)
            }
        }.show(childFragmentManager, "")
    }

}