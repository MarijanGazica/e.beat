package studio.nodroid.ebeat.ui.view

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class DateAxisFormatter : IAxisValueFormatter {

    private val calendar = Calendar.getInstance()
    private val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        calendar.timeInMillis = value.toLong() * 1000
        return sdf.format(calendar.time)
    }
}