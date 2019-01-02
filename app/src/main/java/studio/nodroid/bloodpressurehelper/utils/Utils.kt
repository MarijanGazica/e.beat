package studio.nodroid.bloodpressurehelper.utils

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import studio.nodroid.bloodpressurehelper.model.Date
import studio.nodroid.bloodpressurehelper.model.PressureSeverity
import studio.nodroid.bloodpressurehelper.model.Time
import java.util.*


fun timestampFromTime(date: Date, time: Time): Long {
    val timestampDate = Calendar.getInstance()
    timestampDate.set(date.year, date.month - 1, date.day, time.hour, time.minute)
    return timestampDate.timeInMillis
}

fun getPressureRating(systolic: Int, diastolic: Int): PressureSeverity {
    return when {
        systolic <= 0 || diastolic <= 0 -> PressureSeverity.AWAITING_INPUT
        diastolic >= systolic -> PressureSeverity.ERROR
        systolic > 180 || diastolic > 120 -> PressureSeverity.HYPERTENSION_CRISIS
        systolic >= 140 || diastolic >= 90 -> PressureSeverity.HYPERTENSION_2
        systolic in 130..139 || diastolic in 80..89 -> PressureSeverity.HYPERTENSION_1
        systolic in 120..129 && diastolic < 80 -> PressureSeverity.ELEVATED
        systolic < 120 && diastolic < 80 -> PressureSeverity.NORMAL
        else -> PressureSeverity.ERROR
    }
}

fun View.setBackgroundColorCompat(@ColorRes color: Int) =
    setBackgroundColor(ResourcesCompat.getColor(resources, color, context.theme))

fun hideKeyboard(view: View) {
    val imm = view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun showKeyboard(view: View) {
    view.requestFocus()
    val imm = view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view, 0)
}