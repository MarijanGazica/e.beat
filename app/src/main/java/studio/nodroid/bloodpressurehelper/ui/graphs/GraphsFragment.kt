package studio.nodroid.bloodpressurehelper.ui.graphs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.github.mikephil.charting.components.Legend
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
import studio.nodroid.bloodpressurehelper.utils.DAY
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

    private val systolicDataSet by lazy {
        val set = LineDataSet(mutableListOf(), resources.getString(R.string.systolic))
        set.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        set.color = ResourcesCompat.getColor(resources, R.color.severity_red, requireActivity().theme)
        set.setCircleColor(ResourcesCompat.getColor(resources, R.color.severity_red, requireActivity().theme))
        set.lineWidth = 2f
        set.isHighlightEnabled = false
        set.setDrawValues(false)
        return@lazy set
    }

    private val diastolicDataSet by lazy {
        val set = LineDataSet(mutableListOf(), resources.getString(R.string.diastolic))
        set.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        set.color = ResourcesCompat.getColor(resources, R.color.petroleum, requireActivity().theme)
        set.setCircleColor(ResourcesCompat.getColor(resources, R.color.petroleum, requireActivity().theme))
        set.lineWidth = 2f
        set.isHighlightEnabled = false
        set.setDrawValues(false)
        return@lazy set
    }

    private val pulseDataSet by lazy {
        val set = LineDataSet(mutableListOf(), resources.getString(R.string.pulse))
        set.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        set.color = ResourcesCompat.getColor(resources, R.color.severity_green, requireActivity().theme)
        set.setCircleColor(ResourcesCompat.getColor(resources, R.color.severity_green, requireActivity().theme))
        set.lineWidth = 2f
        set.isHighlightEnabled = false
        set.setDrawValues(false)
        return@lazy set
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        userViewModel.activeUser.observe(this, Observer { viewModel.userSelected(it) })
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

        lineChart.isAutoScaleMinMaxEnabled = true
        lineChart.description = null

        lineChart.legend.form = Legend.LegendForm.CIRCLE
        lineChart.legend.xEntrySpace = 16f

        lineChart.xAxis.granularity = DAY.toFloat()
        lineChart.xAxis.setAvoidFirstLastClipping(false)
        lineChart.xAxis.valueFormatter = DateAxisFormatter()

        viewModel.selectedUserReadings.observe(viewLifecycleOwner, Observer {
            it?.run {
                systolicDataSet.clear()
                diastolicDataSet.clear()
                pulseDataSet.clear()

                systolicDataSet.values = this.map { pressureData -> Entry(pressureData.timestamp.toFloat(), pressureData.systolic.toFloat()) }
                diastolicDataSet.values = this.map { pressureData -> Entry(pressureData.timestamp.toFloat(), pressureData.diastolic.toFloat()) }
                pulseDataSet.values = this.map { pressureData -> Entry(pressureData.timestamp.toFloat(), pressureData.pulse.toFloat()) }

                lineChart.data = LineData(systolicDataSet, diastolicDataSet, pulseDataSet)
                lineChart.xAxis.granularity = 1f
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

}