package studio.nodroid.bloodpressurehelper.utils

import studio.nodroid.bloodpressurehelper.model.Date
import studio.nodroid.bloodpressurehelper.model.Time
import java.util.*

fun timestampFromTime(date: Date, time: Time): Long {
    val timestampDate = Calendar.getInstance()
    timestampDate.set(date.year, date.month, date.day, time.hour, time.minute)
    return timestampDate.timeInMillis
}