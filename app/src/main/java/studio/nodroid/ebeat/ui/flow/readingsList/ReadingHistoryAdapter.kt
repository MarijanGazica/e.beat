package studio.nodroid.ebeat.ui.flow.readingsList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_reading.view.*
import studio.nodroid.ebeat.R
import studio.nodroid.ebeat.model.PressureDataDB
import studio.nodroid.ebeat.utils.toDate
import studio.nodroid.ebeat.utils.toTime

class ReadingHistoryAdapter(private val onReadingSelected: (PressureDataDB) -> Unit) : RecyclerView.Adapter<ReadingHolder>() {

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
        holder.bind(items[position], onReadingSelected)
    }
}

class ReadingHolder(val view: View) : RecyclerView.ViewHolder(view) {

    fun bind(reading: PressureDataDB, onReadingSelected: (PressureDataDB) -> Unit) {
        view.systolic.text = reading.systolic.toString()
        view.diastolic.text = reading.diastolic.toString()
        view.pulse.text = reading.pulse.toString()

        view.range.text = reading.timestamp.toDate()
        view.time.text = reading.timestamp.toTime()

        view.setOnClickListener { onReadingSelected(reading) }
    }
}