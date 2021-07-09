package studio.nodroid.ebeat.utils

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import studio.nodroid.ebeat.model.Date
import studio.nodroid.ebeat.model.DateRange
import studio.nodroid.ebeat.model.PressureSeverity
import studio.nodroid.ebeat.model.Time
import java.text.SimpleDateFormat
import java.util.*

const val DAY = 1000 * 60 * 60 * 24 // 86400000

fun timestampFromTime(date: Date, time: Time): Long {
    val timestampDate = Calendar.getInstance()
    timestampDate.set(date.year, date.month - 1, date.day, time.hour, time.minute)
    return timestampDate.timeInMillis
}

fun getPressureRating(systolic: Int?, diastolic: Int?): PressureSeverity {
    return when {
        diastolic == null || systolic == null || diastolic >= systolic -> PressureSeverity.ERROR
        systolic > 180 || diastolic > 120 -> PressureSeverity.HYPERTENSION_CRISIS
        systolic >= 140 || diastolic >= 90 -> PressureSeverity.HYPERTENSION_2
        systolic in 130..139 || diastolic in 80..89 -> PressureSeverity.HYPERTENSION_1
        systolic in 120..129 && diastolic < 80 -> PressureSeverity.ELEVATED
        systolic < 120 && diastolic < 80 -> PressureSeverity.NORMAL
        else -> PressureSeverity.ERROR
    }
}

fun hideKeyboard(view: View) {
    val imm = view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun showKeyboard(view: View) {
    view.requestFocus()
    val imm = view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view, 0)
}

fun Long.toDate(): String {
    val cal = Calendar.getInstance()
    cal.timeInMillis = this
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return sdf.format(cal.time)
}

fun Long.toTime(): String {
    val cal = Calendar.getInstance()
    cal.timeInMillis = this
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(cal.time)
}

fun Date.toTimestampStart(): Long {
    val calendar = Calendar.getInstance()
    calendar.set(year, month - 1, day, 0, 0, 0)
    return calendar.timeInMillis
}

fun Date.toTimestampEnd(): Long {
    val calendar = Calendar.getInstance()
    calendar.set(year, month - 1, day, 23, 59, 59)
    return calendar.timeInMillis
}

fun getPeriodTimestamps(dayPeriod: Int, date: Date? = null): DateRange {
    val calendar = Calendar.getInstance()
    date?.run {
        calendar.set(date.year, date.month - 1, date.day, 23, 59, 59)
    } ?: calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59)
    val endStamp = calendar.timeInMillis
    val startStamp = endStamp - (dayPeriod.toLong() * DAY)
    return DateRange(startStamp, endStamp)
}