package studio.nodroid.ebeat.ui.view

import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class DateAxisFormatter : ValueFormatter() {

    private val calendar = Calendar.getInstance()
    private val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    override fun getFormattedValue(value: Float): String {
        calendar.timeInMillis = value.toLong()
        return sdf.format(calendar.time)
    }
}