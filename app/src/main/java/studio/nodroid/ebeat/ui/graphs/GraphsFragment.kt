package studio.nodroid.ebeat.ui.graphs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.LargeValueFormatter
import kotlinx.android.synthetic.main.fragment_graphs.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import studio.nodroid.ebeat.R
import studio.nodroid.ebeat.analytics.Analytics
import studio.nodroid.ebeat.analytics.AnalyticsEvent
import studio.nodroid.ebeat.ui.view.DateAxisFormatter
import studio.nodroid.ebeat.ui.view.DatePickerView
import studio.nodroid.ebeat.ui.view.FilterView
import studio.nodroid.ebeat.utils.DAY
import studio.nodroid.ebeat.vm.InputHistoryViewModel
import studio.nodroid.ebeat.vm.UserPickerViewModel

class GraphsFragment : Fragment() {

    private val viewModel: InputHistoryViewModel by viewModel()
    private val userViewModel: UserPickerViewModel by sharedViewModel()
    private val analytics by inject<Analytics>()

    private val systolicDataSet by lazy {
        val set = LineDataSet(mutableListOf(), resources.getString(R.string.systolic))
        set.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        set.color = ResourcesCompat.getColor(resources, R.color.severity_red, requireActivity().theme)
        set.setCircleColor(ResourcesCompat.getColor(resources, R.color.severity_red, requireActivity().theme))
        set.lineWidth = 2f
        set.isHighlightEnabled = false
        set.valueFormatter = LargeValueFormatter()
        return@lazy set
    }

    private val diastolicDataSet by lazy {
        val set = LineDataSet(mutableListOf(), resources.getString(R.string.diastolic))
        set.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        set.color = ResourcesCompat.getColor(resources, R.color.petroleum, requireActivity().theme)
        set.setCircleColor(ResourcesCompat.getColor(resources, R.color.petroleum, requireActivity().theme))
        set.lineWidth = 2f
        set.isHighlightEnabled = false
        set.valueFormatter = LargeValueFormatter()
        return@lazy set
    }

    private val pulseDataSet by lazy {
        val set = LineDataSet(mutableListOf(), resources.getString(R.string.pulse))
        set.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        set.color = ResourcesCompat.getColor(resources, R.color.severity_green, requireActivity().theme)
        set.setCircleColor(ResourcesCompat.getColor(resources, R.color.severity_green, requireActivity().theme))
        set.lineWidth = 2f
        set.isHighlightEnabled = false
        set.valueFormatter = LargeValueFormatter()
        return@lazy set
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_graphs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        filters.onFilterSelected {
            when (it) {
                FilterView.Selection.WEEK -> {
                    analytics.logEvent(AnalyticsEvent.GRAPHS_7)
                    viewModel.weekSelected()
                    viewModel.filterSelected(0)

                }
                FilterView.Selection.MONTH -> {
                    analytics.logEvent(AnalyticsEvent.GRAPHS_30)
                    viewModel.monthSelected()
                    viewModel.filterSelected(1)
                }
                FilterView.Selection.ALL_TIME -> {
                    analytics.logEvent(AnalyticsEvent.GRAPHS_ALL)
                    viewModel.allTimeSelected()
                    viewModel.filterSelected(2)
                }
                FilterView.Selection.RANGE -> {
                    analytics.logEvent(AnalyticsEvent.GRAPHS_RANGE)
                    viewModel.rangeSelected()
                }
            }
        }

        lineChart.isAutoScaleMinMaxEnabled = true
        lineChart.description = null
        lineChart.setVisibleXRangeMinimum(DAY.toFloat())
        lineChart.setVisibleYRangeMinimum(30f, YAxis.AxisDependency.LEFT)

        lineChart.legend.form = Legend.LegendForm.CIRCLE
        lineChart.legend.xEntrySpace = 16f
        lineChart.legend.textSize = 14f
        lineChart.legend.textColor = ResourcesCompat.getColor(resources, R.color.textGray, requireActivity().theme)

        lineChart.xAxis.granularity = DAY.toFloat()
        lineChart.xAxis.setAvoidFirstLastClipping(false)
        lineChart.xAxis.valueFormatter = DateAxisFormatter()
        lineChart.xAxis.gridColor = ResourcesCompat.getColor(resources, R.color.petroleumDark, requireActivity().theme)
        lineChart.xAxis.axisLineColor = ResourcesCompat.getColor(resources, R.color.petroleumDark, requireActivity().theme)
        lineChart.xAxis.textColor = ResourcesCompat.getColor(resources, R.color.textGray, requireActivity().theme)

        lineChart.axisLeft.textSize = 14f
        lineChart.axisLeft.gridColor = ResourcesCompat.getColor(resources, R.color.petroleumDark, requireActivity().theme)
        lineChart.axisLeft.axisLineColor = ResourcesCompat.getColor(resources, R.color.petroleumDark, requireActivity().theme)
        lineChart.axisLeft.textColor = ResourcesCompat.getColor(resources, R.color.textGray, requireActivity().theme)

        lineChart.axisRight.isEnabled = false

        userViewModel.activeUser.observe(viewLifecycleOwner, Observer { viewModel.userSelected(it) })

        viewModel.selectedUserReadings.observe(viewLifecycleOwner, Observer {})
        viewModel.allUserReadings.observe(viewLifecycleOwner, Observer { viewModel.readingsReady() })
        viewModel.selectedDate.observe(viewLifecycleOwner, Observer { /*filters.setDateText(it ?: resources.getString(R.string.range)) */ })
        viewModel.selectedFilter.observe(viewLifecycleOwner, Observer { it?.run { filters.markSelection(this) } })

        viewModel.userReadingsForDate.observe(viewLifecycleOwner, Observer {
            it?.run {

                if (lineChart.visibility == View.GONE && this.isNotEmpty()) {
                    lineChart.visibility = View.VISIBLE
                    emptyListGroup.visibility = View.GONE
                }

                systolicDataSet.clear()
                diastolicDataSet.clear()
                pulseDataSet.clear()

                systolicDataSet.values = this.map { pressureData -> Entry(pressureData.timestamp.toFloat() / 1000, pressureData.systolic.toFloat()) }
                diastolicDataSet.values = this.map { pressureData -> Entry(pressureData.timestamp.toFloat() / 1000, pressureData.diastolic.toFloat()) }
                pulseDataSet.values = this.map { pressureData -> Entry(pressureData.timestamp.toFloat() / 1000, pressureData.pulse.toFloat()) }

                lineChart.data = LineData(systolicDataSet, diastolicDataSet, pulseDataSet)
                lineChart.xAxis.granularity = 1f
                lineChart.fitScreen()
            }

            if (it == null || it.isEmpty()) {
                lineChart.visibility = View.GONE
                emptyListGroup.visibility = View.VISIBLE
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