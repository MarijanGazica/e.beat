package studio.nodroid.bloodpressurehelper.ui.inputHistory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_reading.view.*
import studio.nodroid.bloodpressurehelper.R
import studio.nodroid.bloodpressurehelper.model.PressureDataDB

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
    }
}