package studio.nodroid.bloodpressurehelper.ui.view

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class DateAxisFormatter : IAxisValueFormatter {

    val calendar = Calendar.getInstance()
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        calendar.timeInMillis = value.toLong()
        return sdf.format(calendar.time)
    }
}