package studio.nodroid.ebeat.ui.flow.graphs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.transition.TransitionInflater
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.google.android.material.chip.Chip
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import kotlinx.android.synthetic.main.fragment_graphs.*
import org.koin.android.ext.android.inject
import studio.nodroid.ebeat.R
import studio.nodroid.ebeat.model.User
import studio.nodroid.ebeat.ui.dateTime.DatePickDialog
import studio.nodroid.ebeat.ui.graphs.generateDiastolicGraphLine
import studio.nodroid.ebeat.ui.graphs.generatePulseGraphLine
import studio.nodroid.ebeat.ui.graphs.generateSystolicGraphLine
import studio.nodroid.ebeat.ui.view.DateAxisFormatter
import studio.nodroid.ebeat.utils.DAY
import studio.nodroid.ebeat.utils.dpPx
import studio.nodroid.ebeat.utils.startDotAnimation

class GraphsFragment : Fragment() {

    private val viewModel by inject<GraphsViewModel>()

    private val systolicDataSet by lazy { generateSystolicGraphLine(requireContext()) }
    private val diastolicDataSet by lazy { generateDiastolicGraphLine(requireContext()) }
    private val pulseDataSet by lazy { generatePulseGraphLine(requireContext()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        return inflater.inflate(R.layout.fragment_graphs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        motionLayout.doOnApplyWindowInsets { target, insets, initialState ->
            target.updatePadding(
                bottom = initialState.paddings.bottom + insets.systemWindowInsetBottom,
                top = initialState.paddings.top + insets.systemWindowInsetTop
            )
        }

        motionLayout.transitionToState(R.id.initial)

        icon.startDotAnimation()

        setupGraph()

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

        viewModel.readings.observe(viewLifecycleOwner, Observer { list ->
            if (list != null && list.isNotEmpty()) {
                systolicDataSet.clear()
                diastolicDataSet.clear()
                pulseDataSet.clear()

                val sortedList = list.sortedBy { it.timestamp }
                systolicDataSet.values = sortedList.map { pressureData -> Entry(pressureData.timestamp.toFloat(), pressureData.systolic.toFloat()) }
                diastolicDataSet.values = sortedList.map { pressureData -> Entry(pressureData.timestamp.toFloat(), pressureData.diastolic.toFloat()) }
                pulseDataSet.values = sortedList.map { pressureData -> Entry(pressureData.timestamp.toFloat(), pressureData.pulse.toFloat()) }

                lineChart.data = LineData(systolicDataSet, diastolicDataSet, pulseDataSet)
                lineChart.xAxis.granularity = 1f
                lineChart.fitScreen()
            } else {
                //todo show no readings
            }
        })

        timeAll.setOnClickListener { viewModel.allReadingsSelected() }
        timeMonth.setOnClickListener { viewModel.time30selected() }
        timeRange.setOnClickListener { viewModel.timeRangeSelected() }
        changeSelection.setOnClickListener { viewModel.changeSelectionSelected() }
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

    private fun setupGraph() {
        lineChart.isAutoScaleMinMaxEnabled = true
        lineChart.description = null
        lineChart.setVisibleXRangeMinimum(DAY.toFloat())
        lineChart.setVisibleYRangeMinimum(50f, YAxis.AxisDependency.LEFT)

        lineChart.legend.isEnabled = false
//        lineChart.legend.form = Legend.LegendForm.NONE
//        lineChart.legend.setDrawInside(true)
//        lineChart.legend.xEntrySpace = 16f
//        lineChart.legend.textSize = 14f
//        lineChart.legend.textColor = ResourcesCompat.getColor(resources, R.color.color_on_background, requireActivity().theme)

        lineChart.xAxis.granularity = DAY.toFloat()
        lineChart.xAxis.setAvoidFirstLastClipping(false)
        lineChart.xAxis.valueFormatter = DateAxisFormatter()
        lineChart.xAxis.gridColor = ResourcesCompat.getColor(resources, R.color.transparentWhite, requireActivity().theme)
        lineChart.xAxis.axisLineColor = ResourcesCompat.getColor(resources, R.color.color_on_background, requireActivity().theme)
        lineChart.xAxis.textColor = ResourcesCompat.getColor(resources, R.color.color_on_background, requireActivity().theme)

//        lineChart.axisLeft.textSize = 14f
//        lineChart.axisLeft.setDrawGridLines(false)
        lineChart.axisLeft.gridColor = ResourcesCompat.getColor(resources, R.color.transparentWhite, requireActivity().theme)
        lineChart.axisLeft.axisLineColor = ResourcesCompat.getColor(resources, R.color.color_on_background, requireActivity().theme)
        lineChart.axisLeft.textColor = ResourcesCompat.getColor(resources, R.color.color_on_background, requireActivity().theme)

        lineChart.axisRight.isEnabled = false

    }
}
