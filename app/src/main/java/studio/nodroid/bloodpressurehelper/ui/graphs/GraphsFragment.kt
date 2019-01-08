package studio.nodroid.bloodpressurehelper.ui.graphs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.fragment_graphs.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import studio.nodroid.bloodpressurehelper.R
import studio.nodroid.bloodpressurehelper.ui.view.DateAxisFormatter
import studio.nodroid.bloodpressurehelper.ui.view.DatePickerView
import studio.nodroid.bloodpressurehelper.ui.view.FilterView
import studio.nodroid.bloodpressurehelper.utils.getPeriodTimestamps
import studio.nodroid.bloodpressurehelper.vm.GraphsViewModel
import studio.nodroid.bloodpressurehelper.vm.UserPickerViewModel

class GraphsFragment : Fragment() {

    private val viewModel: GraphsViewModel by viewModel()
    private val userViewModel: UserPickerViewModel by sharedViewModel()

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

        viewModel.selectedUserReadings.observe(this, Observer {
            it?.run {
                val lineDataSet = LineDataSet(
                    this.sortedBy { it.timestamp }
                        .map {
                            Entry(it.timestamp.toFloat(), it.systolic.toFloat())
                        },
                    resources.getString(R.string.systolic)
                )

                lineDataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
                lineDataSet.color = R.color.severity_green
                lineDataSet.lineWidth = 5f
                lineDataSet.setDrawValues(false)
                lineChart.data = LineData(lineDataSet)
                lineChart.invalidate()
            }
        })

        viewModel.allUserReadings.observe(this, Observer { viewModel.readingsReady() })
        viewModel.selectedDate.observe(this, Observer { filters.setDateText(it ?: resources.getString(R.string.date)) })
        viewModel.selectedFilter.observe(this, Observer { it?.run { filters.markSelection(this) } })
        viewModel.userReadingsForDate.observe(this, Observer {
            it?.run {
                // todo show data in graph
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_graphs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        filters.onFilterSelected {

            when (it) {
                FilterView.Selection.WEEK -> {
                    viewModel.rangeSelected(getPeriodTimestamps(7))
                    viewModel.filterSelected(0)
                }
                FilterView.Selection.MONTH -> {
                    viewModel.rangeSelected(getPeriodTimestamps(30))
                    viewModel.filterSelected(1)
                }
                FilterView.Selection.ALL_TIME -> {
                    viewModel.allTimeSelected()
                    viewModel.filterSelected(2)
                }
                FilterView.Selection.DATE -> {
                    datePickerDialog.show(childFragmentManager, "")
                }

            }
        }
        lineChart.xAxis.granularity = 1f
        lineChart.xAxis.valueFormatter = DateAxisFormatter()

    }

}