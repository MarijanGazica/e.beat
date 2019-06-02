package studio.nodroid.ebeat.ui.readingDetails

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.dialog_reading_details.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import studio.nodroid.ebeat.R
import studio.nodroid.ebeat.model.PressureDataDB
import studio.nodroid.ebeat.model.PressureSeverity
import studio.nodroid.ebeat.utils.getPressureRating
import studio.nodroid.ebeat.utils.setBackgroundColorCompat
import studio.nodroid.ebeat.utils.toDate
import studio.nodroid.ebeat.utils.toTime

class ReadingDetailsDialog : DialogFragment() {

    companion object {
        private const val READING_ID = "reading_id"

        fun newInstance(reading: PressureDataDB): ReadingDetailsDialog {
            val fragment = ReadingDetailsDialog()
            val bundle = Bundle()
            bundle.putInt(READING_ID, reading.id)
            fragment.arguments = bundle
            return fragment
        }
    }

    private val readingId by lazy {
        return@lazy arguments?.getInt(READING_ID)
    }

    private val readingDetailsViewModel: ReadingDetailsViewModel by viewModel()

    private val deleteReadingDialog by lazy {
        val dialogBuilder = AlertDialog.Builder(requireActivity())
        dialogBuilder.setPositiveButton(R.string.delete) { _, which -> if (which == DialogInterface.BUTTON_POSITIVE) readingDetailsViewModel.deleteReading() }
        dialogBuilder.setNegativeButton(R.string.cancel) { clickedDialog, which -> if (which == DialogInterface.BUTTON_NEGATIVE) clickedDialog.dismiss() }
        dialogBuilder.setTitle(R.string.delete_reading_title)
        dialogBuilder.setMessage(R.string.delete_reading_description)
        dialogBuilder.create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_reading_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        readingId?.let {
            readingDetailsViewModel.readingSelected(it)
        }

        readingDetailsViewModel.selectedReading.observe(viewLifecycleOwner, Observer {
            it?.let { reading ->
                systolic.text = reading.systolic.toString()
                diastolic.text = reading.diastolic.toString()
                pulse.text = reading.pulse.toString()

                range.text = reading.timestamp.toDate()
                time.text = reading.timestamp.toTime()
                description.text = reading.description
                if (reading.description.isNullOrBlank()) {
                    description.visibility = View.GONE
                } else {
                    description.visibility = View.VISIBLE
                }

                val severityColor = when (getPressureRating(reading.systolic, reading.diastolic)) {
                    PressureSeverity.NORMAL -> R.color.severity_green
                    PressureSeverity.ELEVATED -> R.color.severity_yellow
                    PressureSeverity.HYPERTENSION_1 -> R.color.severity_orange
                    PressureSeverity.HYPERTENSION_2 -> R.color.severity_dark_orange
                    PressureSeverity.HYPERTENSION_CRISIS -> R.color.severity_red
                    else -> R.color.severity_green
                }
                severity.setBackgroundColorCompat(severityColor)
            } ?: dismiss()
        })

        delete.setOnClickListener { deleteReadingDialog.show() }
    }
}