package studio.nodroid.bloodpressurehelper.ui.inputHistory

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import kotlinx.android.synthetic.main.fragment_input_history.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import studio.nodroid.bloodpressurehelper.R
import studio.nodroid.bloodpressurehelper.ui.view.UserPickerDialog
import studio.nodroid.bloodpressurehelper.vm.InputHistoryViewModel
import java.util.*

class InputHistoryFragment : Fragment() {

    private val viewModel: InputHistoryViewModel by viewModel()
    private val readingHistoryAdapter by lazy { ReadingHistoryAdapter() }
    private val userPickerDialog by lazy { UserPickerDialog() }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        viewModel.selectedUserReadings.observe(this, Observer { list ->
            readingHistoryAdapter.setData(list)
            calendarView.removeAllEvents()
            calendarView.addEvents(list.map {
                Event(R.color.colorAccent, it.timestamp)
            })
        })
        viewModel.selectedUser.observe(this, Observer { userName.text = it.name })
        viewModel.allUsers.observe(this, Observer { viewModel.findLastUser() })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_input_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        readingList.layoutManager = LinearLayoutManager(requireContext())
        readingList.adapter = readingHistoryAdapter

        userName.setOnClickListener {
            userPickerDialog.onSelect = viewModel.userSelected
            userPickerDialog.show(fragmentManager, "tag")
        }

        prevMonth.setOnClickListener { calendarView.scrollLeft() }
        nextMonth.setOnClickListener { calendarView.scrollRight() }
        setActiveMonth()

        calendarView.setListener(object : CompactCalendarView.CompactCalendarViewListener {
            override fun onDayClick(dateClicked: Date?) {
            }

            override fun onMonthScroll(selectedMonth: Date?) {
                setActiveMonth(selectedMonth)
            }

        })
    }

    private fun setActiveMonth(date: Date? = null) {
        val calendar = Calendar.getInstance()
        date?.run { calendar.time = date }
        activeMonth.text = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
    }

}