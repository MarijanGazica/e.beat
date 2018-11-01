package studio.nodroid.bloodpressurehelper.ui.view

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
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
        time = it
        setState()
    }

    private val onDateChosen: (Date) -> Unit = {
        dateValue.text = it.toString()
        date = it
        setState()
    }

    private val timeFormat by lazy { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    private val dateFormat by lazy { SimpleDateFormat("dd.MM.yy", Locale.getDefault()) }

    var pressureData: PressureData? = null
    var fragmentManager: FragmentManager? = null

    private var date: Date
    private var time: Time
    private var description = ""

    init {
        LayoutInflater.from(context).inflate(R.layout.view_pressure_input, this, true)

        systolicValue.minValue = 30
        systolicValue.maxValue = 300
        diastolicValue.minValue = 30
        diastolicValue.maxValue = 300
        pulseValue.minValue = 30
        pulseValue.maxValue = 300

        val calendar = Calendar.getInstance()
        date = Date(
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.YEAR)
        )
        time = Time(
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE)
        )

        timeValue.text = timeFormat.format(calendar.time)
        dateValue.text = dateFormat.format(calendar.time)

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
            pressureData = pressureData?.copy(systolic = newValue)
        }

        diastolicValue.setOnValueChangedListener { _, _, newValue ->
            pressureData = pressureData?.copy(diastolic = newValue)
        }

        pulseValue.setOnValueChangedListener { _, _, newValue ->
            pressureData = pressureData?.copy(pulse = newValue)
        }

        inputDescription.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.run {
                    description = this.toString()
                }
            }
        })

        setState()
    }

    private fun setState() {
        pressureData = PressureData(
            systolicValue.value,
            diastolicValue.value,
            pulseValue.value,
            date,
            time,
            timestampFromTime(date, time),
            description
        )
    }
}

fun timestampFromTime(date: Date, time: Time): Long {
    val timestampDate = Calendar.getInstance()
    timestampDate.set(date.year, date.month, date.day, time.hour, time.minute)
    return timestampDate.timeInMillis
}