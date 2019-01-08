package studio.nodroid.bloodpressurehelper.ui.pressureInput

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_pressure_input.*
import kotlinx.android.synthetic.main.view_pressure_input.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import studio.nodroid.bloodpressurehelper.R
import studio.nodroid.bloodpressurehelper.model.PressureSeverity
import studio.nodroid.bloodpressurehelper.ui.view.DatePickerView
import studio.nodroid.bloodpressurehelper.ui.view.TimePickerView
import studio.nodroid.bloodpressurehelper.utils.hideKeyboard
import studio.nodroid.bloodpressurehelper.utils.onIntInputChanged
import studio.nodroid.bloodpressurehelper.utils.onTextChanged
import studio.nodroid.bloodpressurehelper.utils.setSelectedListener
import studio.nodroid.bloodpressurehelper.vm.PressureInputViewModel
import studio.nodroid.bloodpressurehelper.vm.UserPickerViewModel

class PressureInputFragment : Fragment() {

    private val viewModel: PressureInputViewModel by viewModel()
    private val userViewModel: UserPickerViewModel by sharedViewModel()

    private val shakeAnimation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.shake) }

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pressure_input, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        saveReading.setOnClickListener {
            hideKeyboard(it)
            viewModel.saveReadingPressed()
        }

        coordinator.setOnClickListener {
            hideKeyboard(it)
        }

        systolicValue.onIntInputChanged { viewModel.setSystolicValue(it) }
        diastolicValue.onIntInputChanged { viewModel.setDiastolicValue(it) }
        pulseValue.onIntInputChanged { viewModel.setPulseValue(it) }

        inputDescription.onTextChanged { viewModel.setDescription(it) }
        inputDescription.editText?.setOnEditorActionListener { view, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.saveReadingPressed()
                hideKeyboard(view)
                return@setOnEditorActionListener true
            }
            false
        }

        timeValue.setSelectedListener { timePicker.show(childFragmentManager, "time") }
        dateValue.setSelectedListener { datePicker.show(childFragmentManager, "date") }

        viewModel.selectedDate.observe(viewLifecycleOwner, Observer { dateValue.editText?.setText(it, TextView.BufferType.EDITABLE) })
        viewModel.selectedTime.observe(viewLifecycleOwner, Observer { timeValue.editText?.setText(it, TextView.BufferType.EDITABLE) })
        viewModel.pressureSeverity.observe(viewLifecycleOwner, Observer { it?.run { setPressureSeverity(it) } })
        viewModel.screenState.observe(viewLifecycleOwner, Observer { actOnDisplayState(it) })

        userViewModel.activeUser.observe(viewLifecycleOwner, Observer { viewModel.selectedUser = it })
    }

    private fun setPressureSeverity(severity: PressureSeverity) = when (severity) {
        PressureSeverity.ERROR -> {
            severityText.text = getString(R.string.severity_error)
        }
        PressureSeverity.AWAITING_INPUT -> {
            severityText.text = getString(R.string.severity_waiting)
        }
        PressureSeverity.NORMAL -> {
            severityText.text = getString(R.string.severity_normal)
        }
        PressureSeverity.ELEVATED -> {
            severityText.text = getString(R.string.severity_elevated)
        }
        PressureSeverity.HYPERTENSION_1 -> {
            severityText.text = getString(R.string.severity_hypertension1)
        }
        PressureSeverity.HYPERTENSION_2 -> {
            severityText.text = getString(R.string.severity_hypertension2)
        }
        PressureSeverity.HYPERTENSION_CRISIS -> {
            severityText.text = getString(R.string.severity_crisis)
        }
    }

    private fun actOnDisplayState(it: PressureInputViewModel.ScreenState?) {
        when (it) {
            PressureInputViewModel.ScreenState.INPUT_MISSING -> shakeEmptyRequiredInputs()
            PressureInputViewModel.ScreenState.DATA_OK -> {
                hideKeyboard(scrollRoot)
                saveReadingDialog.show()
            }
            PressureInputViewModel.ScreenState.READING_SAVED -> notifyDataSaved()
            PressureInputViewModel.ScreenState.IDLE -> {
            }
        }
    }

    private fun notifyDataSaved() {
        Snackbar.make(coordinator, R.string.reading_saved, Snackbar.LENGTH_SHORT).show()

        systolicValue.editText?.text?.clear()
        diastolicValue.editText?.text?.clear()
        pulseValue.editText?.text?.clear()
        inputDescription.editText?.text?.clear()
    }

    private fun shakeEmptyRequiredInputs() {
        if (systolicValue.editText?.text.isNullOrBlank()) {
            systolicValue.startAnimation(shakeAnimation)
        }
        if (diastolicValue.editText?.text.isNullOrBlank()) {
            diastolicValue.startAnimation(shakeAnimation)
        }
        if (pulseValue.editText?.text.isNullOrBlank()) {
            pulseValue.startAnimation(shakeAnimation)
        }
    }

}