package studio.nodroid.bloodpressurehelper.ui.pressureInput

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_pressure_input.*
import kotlinx.android.synthetic.main.view_pressure_input.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import studio.nodroid.bloodpressurehelper.R
import studio.nodroid.bloodpressurehelper.ui.view.DatePickerView
import studio.nodroid.bloodpressurehelper.ui.view.TimePickerView
import studio.nodroid.bloodpressurehelper.ui.view.UserPickerDialog
import studio.nodroid.bloodpressurehelper.utils.onIntInputChanged
import studio.nodroid.bloodpressurehelper.utils.onTextChanged
import studio.nodroid.bloodpressurehelper.utils.setSelectedListener
import studio.nodroid.bloodpressurehelper.vm.PressureInputViewModel

class PressureInputFragment : Fragment() {

    private val viewModel: PressureInputViewModel by viewModel()

    private val userPickerDialog by lazy { UserPickerDialog().apply { onAddUserSelected = addUserSelected } }

    private val timePicker by lazy {
        TimePickerView().apply {
            this@apply.onTimeChosen = viewModel.timeChosen
        }
    }

    private val datePicker by lazy {
        DatePickerView().apply {
            this@apply.onDateChosen = viewModel.dateChosen
        }
    }

    private val saveReadingDialog by lazy {
        AlertDialog.Builder(requireContext())
            .setPositiveButton(R.string.yes) { _, _ -> viewModel.saveReading() }
            .setNegativeButton(R.string.no) { _, _ -> }
            .setTitle(R.string.are_you_sure)
            .create()
    }

    private val addUserSelected: () -> Unit = {
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        viewModel.allUsers.observe(this, Observer { viewModel.findLastUser() })
        viewModel.selectedDate.observe(this, Observer { dateValue.editText?.setText(it, TextView.BufferType.EDITABLE) })
        viewModel.selectedTime.observe(this, Observer { timeValue.editText?.setText(it, TextView.BufferType.EDITABLE) })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pressure_input, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        saveReading.setOnClickListener { saveReadingDialog.show() }

        systolicValue.onIntInputChanged { viewModel.systolicValue = it }
        diastolicValue.onIntInputChanged { viewModel.diastolicValue = it }
        pulseValue.onIntInputChanged { viewModel.pulseValue = it }

//        systolicValue.activateDrawableTinter()
//        diastolicValue.activateDrawableTinter()
//        pulseValue.activateDrawableTinter()

        inputDescription.onTextChanged { viewModel.description = it }
        inputDescription.editText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                saveReadingDialog.show()
                return@setOnEditorActionListener true
            }
            false

        }

        timeValue.setSelectedListener { timePicker.show(childFragmentManager, "time") }
        dateValue.setSelectedListener { datePicker.show(childFragmentManager, "date") }

    }


}