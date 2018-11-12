package studio.nodroid.bloodpressurehelper.model

import java.util.*

data class Time(val hour: Int, val minute: Int) {
    override fun toString(): String = "${hour.timeDisplayValue()}:${minute.timeDisplayValue()}"
}

fun Int.timeDisplayValue(): String = this.toString().padStart(2, '0')

fun Long.timestampToTime(): Time {
    val cal = Calendar.getInstance()
    cal.timeInMillis = this
    return Time(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
}