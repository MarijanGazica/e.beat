package studio.nodroid.ebeat.ui.view

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import studio.nodroid.ebeat.model.Date
import java.util.*

class DatePickerView : DialogFragment(), DatePickerDialog.OnDateSetListener {

    var onDateChosen: (Date) -> Unit = {}
    var onDismiss: () -> Unit = {}

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it
        return DatePickerDialog(requireContext(), this, year, month, day)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        onDateChosen(Date(year, month + 1, day))
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        onDismiss()
    }
}