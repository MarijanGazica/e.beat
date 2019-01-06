package studio.nodroid.bloodpressurehelper.ui.inputHistory

import android.content.Context
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
import studio.nodroid.bloodpressurehelper.R
import studio.nodroid.bloodpressurehelper.ui.view.DatePickerView
import studio.nodroid.bloodpressurehelper.utils.getPeriodTimestamps
import studio.nodroid.bloodpressurehelper.vm.InputHistoryViewModel
import studio.nodroid.bloodpressurehelper.vm.UserPickerViewModel

class ReadingHistoryFragment : Fragment() {

    private val viewModel: InputHistoryViewModel by viewModel()
    private val userViewModel: UserPickerViewModel by sharedViewModel()
    private val readingHistoryAdapter by lazy { ReadingHistoryAdapter() }
    private val datePickerDialog by lazy {
        DatePickerView().apply {
            this@apply.onDateChosen = {
                viewModel.dateSelected(it)
                viewModel.filterSelected(3)
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        userViewModel.activeUser.observe(this, Observer { viewModel.userSelected(it) })

        viewModel.selectedUserReadings.observe(this, Observer {})
        viewModel.allUserReadings.observe(this, Observer { viewModel.readingsReady() })
        viewModel.userReadingsForDate.observe(this, Observer { it?.run { readingHistoryAdapter.setData(this) } })
        viewModel.selectedDate.observe(this, Observer { date.text = it ?: resources.getString(R.string.date) })
        viewModel.selectedFilter.observe(this, Observer { it?.run { markSelection(this) } })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_input_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inputList.layoutManager = LinearLayoutManager(requireContext())
        inputList.adapter = readingHistoryAdapter

        week.setOnClickListener {
            viewModel.rangeSelected(getPeriodTimestamps(7))
            viewModel.filterSelected(0)
        }
        month.setOnClickListener {
            viewModel.rangeSelected(getPeriodTimestamps(30))
            viewModel.filterSelected(1)
        }
        allTime.setOnClickListener {
            viewModel.allTimeSelected()
            viewModel.filterSelected(2)
        }
        date.setOnClickListener {
            date.isChecked = false
            datePickerDialog.show(childFragmentManager, "")
        }
    }

    private fun markSelection(position: Int) {
        week.isChecked = position == 0
        month.isChecked = position == 1
        allTime.isChecked = position == 2
        date.isChecked = position == 3
    }
}