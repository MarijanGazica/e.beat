package studio.nodroid.bloodpressurehelper.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import kotlinx.android.synthetic.main.view_pressure_input.view.*
import studio.nodroid.bloodpressurehelper.R
import studio.nodroid.bloodpressurehelper.model.Date
import studio.nodroid.bloodpressurehelper.model.PressureData
import studio.nodroid.bloodpressurehelper.model.Time
import java.text.SimpleDateFormat
import java.util.*


class PressureInputView(context: Context, attrs: AttributeSet? = null) :
    ConstraintLayout(context, attrs) {

    private val timePicker by lazy {
        TimePickerView().apply {
            this@apply.onTimeChosen = this@PressureInputView.onTimeChosen
        }
    }

    private val datePicker by lazy {
        DatePickerView().apply {
            this@apply.onDateChosen = this@PressureInputView.onDateChosen
        }
    }

    private val onTimeChosen: (Time) -> Unit = {
        timeValue.text = it.toString()
        pressureData.value = pressureData.value?.copy(time = it)
    }

    private val onDateChosen: (Date) -> Unit = {
        dateValue.text = it.toString()
        pressureData.value = pressureData.value?.copy(date = it)
    }

    private val timeFormat by lazy { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    private val dateFormat by lazy { SimpleDateFormat("dd.MM.yy", Locale.getDefault()) }

    val pressureData = MutableLiveData<PressureData>()
    var fragmentManager: FragmentManager? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_pressure_input, this, true)

        systolicValue.minValue = 30
        systolicValue.maxValue = 300
        diastolicValue.minValue = 30
        diastolicValue.maxValue = 300
        pulseValue.minValue = 30
        pulseValue.maxValue = 300

        val calendar = Calendar.getInstance()
        val date = Date(
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.YEAR)
        )
        val time = Time(
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE)
        )

        timeValue.text = timeFormat.format(calendar.time)
        dateValue.text = dateFormat.format(calendar.time)

        pressureData.value = PressureData(
            systolicValue.value,
            diastolicValue.value,
            pulseValue.value,
            date,
            time
        )

        timeValue.setOnClickListener {
            fragmentManager?.run {
                timePicker.show(this, "time")
            }
        }

        dateValue.setOnClickListener {
            fragmentManager?.run {
                datePicker.show(this, "time")
            }
        }

        systolicValue.setOnValueChangedListener { _, _, newValue ->
            pressureData.value = pressureData.value?.copy(systolic = newValue)
        }

        diastolicValue.setOnValueChangedListener { _, _, newValue ->
            pressureData.value = pressureData.value?.copy(diastolic = newValue)
        }

        pulseValue.setOnValueChangedListener { _, _, newValue ->
            pressureData.value = pressureData.value?.copy(pulse = newValue)
        }
    }
}