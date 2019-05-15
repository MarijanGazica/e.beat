package studio.nodroid.ebeat.ui.view

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import studio.nodroid.ebeat.model.Time
import java.util.*

class TimePickerView : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    var onTimeChosen: (Time) -> Unit = {}

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(activity, this, hour, minute, true)
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        onTimeChosen(Time(hourOfDay, minute))
    }
}