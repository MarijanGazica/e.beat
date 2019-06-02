package studio.nodroid.ebeat.ui.inputHistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_input_history.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import studio.nodroid.ebeat.R
import studio.nodroid.ebeat.ads.inflateAd
import studio.nodroid.ebeat.analytics.Analytics
import studio.nodroid.ebeat.analytics.AnalyticsEvent
import studio.nodroid.ebeat.model.PressureDataDB
import studio.nodroid.ebeat.sharedPrefs.AdStatus
import studio.nodroid.ebeat.ui.dateTime.DatePickDialog
import studio.nodroid.ebeat.ui.readingDetails.ReadingDetailsDialog
import studio.nodroid.ebeat.ui.userPicker.UserPickerViewModel
import studio.nodroid.ebeat.ui.view.FilterView


class InputHistoryFragment : Fragment() {

    private val viewModel: InputHistoryViewModel by viewModel()
    private val userViewModel: UserPickerViewModel by sharedViewModel()

    private val analytics by inject<Analytics>()

    private val readingHistoryAdapter by lazy { ReadingHistoryAdapter(onReadingSelected) }

    private val onReadingSelected: (PressureDataDB) -> Unit = {
        ReadingDetailsDialog.newInstance(it).show(childFragmentManager, "readingDetails")
    }

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
                    analytics.logEvent(AnalyticsEvent.LIST_7)
                    viewModel.weekSelected()
                    viewModel.filterSelected(0)
                }
                FilterView.Selection.MONTH -> {
                    analytics.logEvent(AnalyticsEvent.LIST_30)
                    viewModel.monthSelected()
                    viewModel.filterSelected(1)
                }
                FilterView.Selection.ALL_TIME -> {
                    analytics.logEvent(AnalyticsEvent.LIST_ALL)
                    viewModel.allTimeSelected()
                    viewModel.filterSelected(2)
                }
                FilterView.Selection.RANGE -> {
                    analytics.logEvent(AnalyticsEvent.LIST_RANGE)
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
            it?.sortedByDescending { item -> item.timestamp }
                ?.run { readingHistoryAdapter.setData(this) }
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

        viewModel.adStatus.observe(viewLifecycleOwner, Observer {
            it?.run {
                when (it) {
                    AdStatus.NON_PERSONALISED -> adView.inflateAd(false)
                    AdStatus.PERSONALISED -> adView.inflateAd(true)
                    AdStatus.DISABLED -> {
                    }
                }
            }
        })
    }

    private fun showDatePickerDialog() {
        DatePickDialog().apply {
            this@apply.onDateChosen = {
                viewModel.dateSelected(it)
                viewModel.filterSelected(3)
            }
        }.show(childFragmentManager, "")
    }

}