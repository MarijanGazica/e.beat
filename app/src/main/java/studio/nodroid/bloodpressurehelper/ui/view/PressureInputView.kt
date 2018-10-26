package studio.nodroid.bloodpressurehelper.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.view_pressure_input.view.*
import studio.nodroid.bloodpressurehelper.R
import java.text.SimpleDateFormat
import java.util.*


class PressureInputView(context: Context, attrs: AttributeSet? = null) :
    ConstraintLayout(context, attrs) {

    private val timeFormat by lazy { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    private val dateFormat by lazy { SimpleDateFormat("dd.MM.yy", Locale.getDefault()) }

    init {
        initializeView()
    }

    private fun initializeView() {
        LayoutInflater.from(context).inflate(R.layout.view_pressure_input, this, true)

        systolicValue.minValue = 30
        systolicValue.maxValue = 300
        diastolicValue.minValue = 30
        diastolicValue.maxValue = 300
        pulseValue.minValue = 30
        pulseValue.maxValue = 300

        val calendar = Calendar.getInstance()
        timeValue.text = timeFormat.format(calendar.time)
        dateValue.text = dateFormat.format(calendar.time)
    }
}