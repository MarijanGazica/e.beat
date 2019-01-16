package studio.nodroid.ebeat.ui.inputHistory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_reading.view.*
import studio.nodroid.ebeat.R
import studio.nodroid.ebeat.model.PressureDataDB
import studio.nodroid.ebeat.model.PressureSeverity
import studio.nodroid.ebeat.utils.getPressureRating
import studio.nodroid.ebeat.utils.setBackgroundColorCompat
import studio.nodroid.ebeat.utils.toDate
import studio.nodroid.ebeat.utils.toTime

class ReadingHistoryAdapter : RecyclerView.Adapter<ReadingHolder>() {

    private val items = mutableListOf<PressureDataDB>()

    override fun getItemCount() = items.size

    fun setData(data: List<PressureDataDB>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReadingHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reading, parent, false)
        return ReadingHolder(view)
    }

    override fun onBindViewHolder(holder: ReadingHolder, position: Int) {
        holder.bind(items[position])
    }
}

class ReadingHolder(val view: View) : RecyclerView.ViewHolder(view) {

    fun bind(reading: PressureDataDB) {
        view.systolic.text = reading.systolic.toString()
        view.diastolic.text = reading.diastolic.toString()
        view.pulse.text = reading.pulse.toString()

        view.date.text = reading.timestamp.toDate()
        view.time.text = reading.timestamp.toTime()
        view.description.text = reading.description
        if (reading.description.isNullOrBlank()) {
            view.description.visibility = View.GONE
        } else {
            view.description.visibility = View.VISIBLE
        }

        val severityColor = when (getPressureRating(reading.systolic, reading.diastolic)) {
            PressureSeverity.NORMAL -> R.color.severity_green
            PressureSeverity.ELEVATED -> R.color.severity_yellow
            PressureSeverity.HYPERTENSION_1 -> R.color.severity_orange
            PressureSeverity.HYPERTENSION_2 -> R.color.severity_dark_orange
            PressureSeverity.HYPERTENSION_CRISIS -> R.color.severity_red
            else -> R.color.severity_green
        }
        view.severity.setBackgroundColorCompat(severityColor)

    }
}