package studio.nodroid.bloodpressurehelper.ui.view

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.view_pressure_input.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import studio.nodroid.bloodpressurehelper.R
import studio.nodroid.bloodpressurehelper.model.*
import studio.nodroid.bloodpressurehelper.model.Date
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
        setTime(it)
        timer.cancel()
    }

    private fun setTime(it: Time) {
        timeValue.text = it.toString()
        time = it
        setState()
    }

    private val onDateChosen: (Date) -> Unit = {
        setDate(it.copy(month = it.month - 1))
        timer.cancel()
    }

    private fun setDate(it: Date) {
        dateValue.text = it.toString()
        date = it
        setState()
    }

    private val timeFormat by lazy { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    private val dateFormat by lazy { SimpleDateFormat("d.MM.yyyy", Locale.getDefault()) }

    var pressureData: PressureData? = null
    var fragmentManager: FragmentManager? = null

    private var date: Date
    private var time: Time
    private var description = ""

    private var timer = GlobalScope.launch(Dispatchers.Main) {
        while (true) {
            setTime(System.currentTimeMillis().timestampToTime())
            delay(5000)
        }
    }

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
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
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

    override fun onDetachedFromWindow() {
        timer.cancel()
        super.onDetachedFromWindow()
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

    fun setData(data: PressureInfo?) = data?.run {
        systolicValue.value = systolic
        diastolicValue.value = diastolic
        pulseValue.value = pulse
        setState()
    }
}

fun timestampFromTime(date: Date, time: Time): Long {
    val timestampDate = Calendar.getInstance()
    timestampDate.set(date.year, date.month, date.day, time.hour, time.minute)
    Log.d("findme", date.month.toString())
    return timestampDate.timeInMillis
}